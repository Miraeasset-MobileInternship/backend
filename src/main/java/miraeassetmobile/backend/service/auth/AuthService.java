package miraeassetmobile.backend.service.auth;


import miraeassetmobile.backend.config.security.jwt.TokenProvider;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.auth.*;
import miraeassetmobile.backend.domain.dto.auth.sms.PhoneNumberCode;
import miraeassetmobile.backend.domain.dto.auth.sms.SmsAuthUtil;
import miraeassetmobile.backend.domain.dto.auth.token.LogoutAccessToken;
import miraeassetmobile.backend.domain.dto.auth.token.RefreshToken;
import miraeassetmobile.backend.domain.dto.auth.token.TokenDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.ProfileImg;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.domain.enums.UserTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.ProfileImgRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import miraeassetmobile.backend.repository.redis.LogoutAccessTokenRedisRepository;
import miraeassetmobile.backend.repository.redis.PhoneNumberCodeRedisRepository;
import miraeassetmobile.backend.repository.redis.RefreshTokenRedisRepository;
import miraeassetmobile.backend.service.ResponseService;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class AuthService {



    AuthenticationManagerBuilder authenticationManagerBuilder;

    UserInfoRepository userInfoRepository;
    ClassRepository classRepository;
    StudentRepository studentRepository;
    ProfileImgRepository profileImgRepository;
    TokenProvider tokenProvider;

    RefreshTokenRedisRepository refreshTokenRedisRepository;
    LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    SmsAuthUtil smsAuthUtil;
    PhoneNumberCodeRedisRepository phoneNumberCodeRedisRepository;
    ResponseService responseService;


    AuthService(ResponseService responseService, ProfileImgRepository profileImgRepository, PhoneNumberCodeRedisRepository phoneNumberCodeRedisRepository, SmsAuthUtil smsAuthUtil, LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository, RefreshTokenRedisRepository refreshTokenRedisRepository, AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider, UserInfoRepository userInfoRepository,  ClassRepository classRepository, StudentRepository studentRepository){
        this.classRepository=classRepository;
        this.studentRepository=studentRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder=authenticationManagerBuilder;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.logoutAccessTokenRedisRepository =logoutAccessTokenRedisRepository;
        this.smsAuthUtil=smsAuthUtil;
        this.phoneNumberCodeRedisRepository = phoneNumberCodeRedisRepository;
        this.profileImgRepository = profileImgRepository;
        this.responseService = responseService;
        this.userInfoRepository = userInfoRepository;
    }




    public UserInfo getUserByPhone(String phoneNumber){
        return userInfoRepository.findByPhoneNum(phoneNumber).get();
    }


    //2가지 리턴 경우의 수가 존재함..
    @Transactional
    public BanklassResponseEntity getStart(SignInRequestDto signInRequestDto){


        //1. 핸드폰 번호 유효성 인증 (코드가 맞는지 체크)
        checkValidCode(signInRequestDto.getPhoneNum(),signInRequestDto.getCode());



        //2. 유효한 핸드폰 번호 인경우

        //1) 미가입자 오류
//        가입안된 유저임 -> 303 SEE OTHER return
        if(!userInfoRepository.existsByPhoneNum(signInRequestDto.getPhoneNum())){
            //핸드폰 번호 포함-가입을 다시 진행하라는 의미

            throw new ServiceException(ErrorCode.SIGN_UP_REQUIRED); //이게안된다
        }


        //2) 가입된 유저인 경우(로그인)
        TokenDto t = login(signInRequestDto.getPhoneNum()); //이건잘딤

        return responseService.successHandler(createSignInInfo(t));

    }


    @Transactional
    public BanklassResponseEntity startWithSignUp(SignUpRequestDto signUpRequestDto){


        //1. 가입시키기


        //1) 가입 정보가 있는 유저인지 확인
        responseService.alreadyExistUser(signUpRequestDto.getPhoneNum());



        //2) 가입진행
        signUp(signUpRequestDto);



        //2. 로그인
        TokenDto t = login(signUpRequestDto.getPhoneNum());



        //3. 필요정보 수집
        return responseService.successHandler(createSignInInfo(t));


//        return null;
        /*
        필요정보

        1. 인증정보
            1) aceessToken -> header저장
            2) grantType
            3) accessToken -> 만료 시간


        userInfo
            1) 이름
            2) 역할
            3) 반 정보

        --선생님인 경우
        :제작한 반의 정보 포함된 리스트

        -- 학생의 경우
        : 속한 반의 정보 포함된 리스트

         */


    }


    //폰 번호로 로그인 시키기
    @Transactional
    public TokenDto login(String phoneNumber){



        //1. 다른 방식으로 인증했으므로 비밀번호 필요 없음
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber,"", AuthorityUtils.createAuthorityList("USER"));
        //미인증 객체인 UserPasswordAuthenticationToken생성


        //미인증 객체를 받아
        //2. 유효한 유저인지 검증 -> 여기서 검증막힌다 유효하지 않다고 나옴
        /*
        에러원인 : 비밀번호를 사용하지 않아서 "" 를 넣었는데, (null로는안된더..)
        자동으로 passwordEncoder를 시큐리티가 내장하고 있어서 비번이 맞지 않았음 -> userdetailservice 에 있는 password와 위의 credential이 일치해야함

        그래서 encoder를 ㅏㅅ용하지 않는 설정을 security config에 넣어줌

         */

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);


        // 3. token 생성
        TokenDto tokenDto = tokenProvider.generateToken(authentication);
        // 4. refresh token 생성 ( database 및 redis 저장을 위한 refresh token )



        //회사에서 redis를 쓸수있는가...(일단 로컬로 진행중)
        RefreshToken refreshToken = RefreshToken.builder()
                .id(authentication.getName()) //어느 유저의 리프레시 토큰인가
                .refreshToken(tokenDto.getRefreshToken())//실제 리프레시 토큰
                .expiration(tokenProvider.getRefreshTokenRemainExpiration())//만료시간
                .build();


        try {

            // 5. redis에 토큰 저장
            refreshTokenRedisRepository.save(refreshToken);


            return tokenDto;

        }catch (Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_LOGIN);
        }
    }




    //가입
    public void signUp(SignUpRequestDto signUpRequestDto){

        /*
        save가 안됩니다 -> 근데 id return은 잘되는데 데베에는 없다 -> Transaction annotation 걸어놔서 그런거였음
         */
        try {
            //유저 가입
            UserInfo newUser = signUpRequestDto.toUser(signUpRequestDto.getPhoneNum(), signUpRequestDto.getUserName(), signUpRequestDto.getUserRole(), signUpRequestDto.getProfileImgId());
            UserInfo u = userInfoRepository.save(newUser);

        }catch(Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_SIGNUP);
        }

//        return createUri(u.getId(), UriTypes.USER );

    }





    //로그인된 유저에게 반환해주어야 하는 정보들
    @Transactional
    public SignInResponseDto createSignInInfo(TokenDto tokenDto){

        AccessTokenInfo accessTokenInfo = AccessTokenInfo.builder()
                .accessToken(tokenDto.getAccessToken())
                .grantType(tokenDto.getGrantType())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .refreshToken(tokenDto.getRefreshToken())
                .build();


        UserInfo u = userInfoRepository.findById(tokenDto.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));


        List<ClassOnboardInfo> classOnboardInfos = new ArrayList<>();

        if(u.getUserRole().equals(UserTypes.TEACHER.getTypeName())){
            //선생님의 학급 리스트
            List<Classes> classes = classRepository.findByTeacherId(u.getId());

            for (Classes c: classes) {

                ClassOnboardInfo cInfo = ClassOnboardInfo.builder()
                        .classId(c.getId())
                        .title(c.getTitle())
                        .grade(c.getGrade() +"학년 "+c.getClassNum()+"반")
                        .teacherId(c.getTeacherId())
                        .currency(c.getCurrency())
                        .createTimestamp(c.getCreateTimestamp())
                        .build();



                classOnboardInfos.add(cInfo);
            }

        }else{


            List<Student> studentClassList = studentRepository.findByUserId(u.getId());

            for (Student s: studentClassList) {

                Long classId = s.getClassId(); //학생이 속한 반의 id


                //해당 반의 id로 반의 정보를 끌어오기 (1개)
                Classes c = classRepository.findById(classId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));

                ClassOnboardInfo cInfo = ClassOnboardInfo.builder()
                        .classId(c.getId())
                        .title(c.getTitle())
                        .grade(c.getGrade() +"학년 "+c.getClassNum()+"반")
                        .teacherId(c.getTeacherId())
                        .currency(c.getCurrency())
                        .createTimestamp(c.getCreateTimestamp())
                        .build();

                classOnboardInfos.add(cInfo);

            }

        }


        ProfileImg p = profileImgRepository.findById(u.getProfileImgId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_IMAGE));



        UserOnboardInfo userOnboardInfo = UserOnboardInfo.builder()
                .userId(u.getId())
                .userName(u.getUserName())
                .userRole(u.getUserRole())
                .profileImg(p.getIconCode())
                .classInfo(classOnboardInfos)
                .build();


        return SignInResponseDto.builder()
                .tokenInfo(accessTokenInfo)
                .userInfo(userOnboardInfo)
                .build();
    }



    @Transactional
    public BanklassResponseEntity logout(HttpServletRequest request) {

        // 1. Request Header 에서 access token 빼기
        String accessToken = tokenProvider.resolveToken(request);

        Long userId = getUserIdFromAccessToken(accessToken);

        //logout token의 TTL은 access token의 남은 기간동안 유지되어야 함
        long remainAccessTokenExpiration = tokenProvider.getRemainExpiration(accessToken);

        try {

            //redis에 존재하는 refreshToken 삭제
            refreshTokenRedisRepository.deleteById(userId.toString());


            //logout token를 redis에 저장 (이후 로그아웃된 유저의 AccessToken으로 접근 방지)
            logoutAccessTokenRedisRepository.save(
                    LogoutAccessToken.of(accessToken, userId, remainAccessTokenExpiration));


            HashMap<String, String> result = new HashMap<String, String>();
            result.put("message", "로그아웃이 정상적으로 수행되었습니다.");

            return responseService.successHandler(
                    result
            );
        }catch(Exception e){
            throw new ServiceException(ErrorCode.LOGOUT_ERROR);
        }

    }


    @Transactional
    public BanklassResponseEntity reissue(HttpServletRequest request, String refreshToken) {

        // 1. Refresh token 검증
        // refreshtoken의 validation을 검증할 필요는 없음
//        if (!tokenProvider.validateToken(refreshToken)) {
//            throw new ServiceException(ErrorCode.INVALID_TOKEN);
//        }

        // 2. Request Header 에서 access toke 추출
        String accessToken = tokenProvider.resolveToken(request);



        // 3. Access Token이 만료된 경우 동일 유저의 정보로 새로운 authentication 생성
        Authentication authentication = tokenProvider.getAuthentication(
                accessToken);

        // 4. redis에서 userId를(id) 기반으로 Refresh Token 값 가져오기
        RefreshToken storedRefreshToken = refreshTokenRedisRepository.findById(
                        authentication.getName())
                .orElseThrow(() -> new ServiceException(ErrorCode.LOGOUT_USER)); //SERVER ERROR(?)

        // 4. Refresh Token 일치 여부 검사 (프론트에서 보유한 refresh토큰과 레디스에 저장해둔 정보가 일치하는가)
        if (!storedRefreshToken.getRefreshToken().equals(refreshToken)) {
            throw new ServiceException(ErrorCode.INVALID_REFRESH_TOKEN);
        }



        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateToken(authentication);


        try {

            //6. redis에 존재하는 refreshToken 삭제
            refreshTokenRedisRepository.deleteById(authentication.getName());


            //7. 새로운 refresh토큰으로 다시저장
            RefreshToken updatedRefreshToken = RefreshToken.builder()
                    .id(authentication.getName()) //어느 유저의 리프레시 토큰인가
                    .refreshToken(tokenDto.getRefreshToken())//새로 생성된 리프래시 토큰
                    .expiration(tokenProvider.getRefreshTokenRemainExpiration())//만료시간
                    .build();


            // 5. redis에 토큰 저장
            refreshTokenRedisRepository.save(updatedRefreshToken);


            // 새로운 토큰 정보를 이용해
            return responseService.successHandler(
                    AccessTokenInfo.builder()
                            .grantType(tokenDto.getGrantType())
                            .accessToken(tokenDto.getAccessToken())
                            .refreshToken(tokenDto.getRefreshToken())
                            .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                            .build()
            );

        }catch (Exception e){
            throw new ServiceException(ErrorCode.TOKEN_REISSUE_ERROR);
        }

    }


    public Long getUserIdFromAccessToken(String accessToken){

        // access token 유효성 검사
        if (!tokenProvider.validateToken(accessToken)) {
            throw new ServiceException(ErrorCode.INVALID_TOKEN);
        }

        // access token으로부터 userId 가져오기
        String userId = tokenProvider.getAuthentication(accessToken).getName();


        return Long.parseLong(userId);

    }



    //문자전송
    public BanklassResponseEntity sendMessage(String toNumber) {

        Message coolsms = new Message(smsAuthUtil.getApiKey(), smsAuthUtil.getApiSecret());

        String code = createCode();

        try {

            //redis에 3분 유효기간으로 저장
            phoneNumberCodeRedisRepository.save(PhoneNumberCode.builder()
                    .id(toNumber)
                    .code(code)
                    .expiration(smsAuthUtil.getExpiration())
                    .build());

        }catch(Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_CODE);
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", toNumber);
        params.put("from", smsAuthUtil.getFromNumber());
        params.put("type", "SMS");
        params.put("text", "[MiraeAsset:Banklass]\n인증번호 ["+ code +"]를 입력하세요.\n인증번호는 3분 동안만 유효합니다.");
        params.put("app_version", "test app 1.0"); // application name and version

        try {

            JSONObject obj = (JSONObject) coolsms.send(params);

            //정상적으로 작동했으나 에러인 경우들이 있다
            // https://docs.coolsms.co.kr/api-reference/message-status-codes
            // error_count가 0개가 아니면 에러로 반환해야함

            String result = obj.get("error_count").toString();

            if(!result.equals("0")){
                throw new ServiceException(ErrorCode.MESSAGE_SERVER_ERROR);
            }

            System.out.println(obj.toString());


        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            throw new ServiceException(ErrorCode.MESSAGE_SERVER_ERROR);
        }

        HashMap<String,String> result = new HashMap<String,String>();
        result.put("message","메세지가 성공적으로 전송되었습니다.");

        return responseService.successHandler(
            result
        );


    }




    //6자리 랜덤 암호 만들기
    public String createCode(){

        // 숫자만으로 랜덤번호 생성
        String numList = "0123456789";
        int alphaNumLength = numList.length();

        Random random = new Random();

        StringBuffer code = new StringBuffer();
        for (int i = 0; i < 6; i++) { //6자리암호
            code.append(numList.charAt(random.nextInt(alphaNumLength)));
        }

        return code.toString();

    }



    //6자리 코드가 맞는지 검사하기
    public boolean checkValidCode(String phoneNumber, String code){

        // redis에서 번호를 이용해 가져오기
        PhoneNumberCode phoneNumberCode = phoneNumberCodeRedisRepository.findById(phoneNumber)
                .orElseThrow(() -> new ServiceException(ErrorCode.INVALID_CODE));

        // code 일치 여부 검사 (프론트에서 보유한 refresh토큰과 레디스에 저장해둔 정보가 일치하는가)
        if (!phoneNumberCode.getCode().equals(code)) {
            throw new ServiceException(ErrorCode.INCORRECT_CODE);
        }

        return true;

    }



}
