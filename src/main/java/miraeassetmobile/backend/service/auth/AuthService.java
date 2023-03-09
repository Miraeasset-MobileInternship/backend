package miraeassetmobile.backend.service.auth;

import miraeassetmobile.backend.config.security.jwt.TokenProvider;
import miraeassetmobile.backend.domain.dto.auth.SignInRequestDto;
import miraeassetmobile.backend.domain.dto.auth.token.LogoutAccessToken;
import miraeassetmobile.backend.domain.dto.auth.token.RefreshToken;
import miraeassetmobile.backend.domain.dto.auth.token.TokenDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.UserInfo;

import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.domain.enums.UserTypes;
import miraeassetmobile.backend.error.exception.CustomLoginException;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;

import miraeassetmobile.backend.domain.dto.auth.SignInResponseDto;
import miraeassetmobile.backend.domain.dto.auth.SignUpRequestDto;
import miraeassetmobile.backend.domain.dto.auth.AccessTokenInfo;
import miraeassetmobile.backend.domain.dto.auth.ClassOnboardInfo;
import miraeassetmobile.backend.domain.dto.auth.UserOnboardInfo;

import miraeassetmobile.backend.repository.redis.LogoutAccessTokenRedisRepository;
import miraeassetmobile.backend.repository.redis.RefreshTokenRedisRepository;
import miraeassetmobile.backend.service.ErrorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthService {



    AuthenticationManagerBuilder authenticationManagerBuilder;
    ErrorService errorService;
    UserInfoRepository userInfoRepository;
    ClassRepository classRepository;
    StudentRepository studentRepository;
    TokenProvider tokenProvider;

    RefreshTokenRedisRepository refreshTokenRedisRepository;
    LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;


    AuthService(LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository, RefreshTokenRedisRepository refreshTokenRedisRepository,AuthenticationManagerBuilder authenticationManagerBuilder,TokenProvider tokenProvider, UserInfoRepository userInfoRepository, ErrorService errorService, ClassRepository classRepository, StudentRepository studentRepository){
        this.userInfoRepository =userInfoRepository;
        this.errorService=errorService;
        this.classRepository=classRepository;
        this.studentRepository=studentRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder=authenticationManagerBuilder;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.logoutAccessTokenRedisRepository =logoutAccessTokenRedisRepository;
    }



    public UserInfo getUser(Long userId){

        UserInfo u = userInfoRepository.findById(userId).get();

        System.out.println(u.getUserName());

        return u;
    }

    public UserInfo getUserByPhone(String phoneNumber){
        return userInfoRepository.findByPhoneNum(phoneNumber).get();
    }


    //2가지 리턴 경우의 수가 존재함..
    @Transactional
    public SignInResponseDto getStart(SignInRequestDto signInRequestDto){
//        가입안된 유저임 -> 303 SEE OTHER return
        if(!userInfoRepository.existsByPhoneNum(signInRequestDto.getPhoneNum())){
            //핸드폰 번호 포함-가입을 다시 진행하라는 의미
            throw new CustomLoginException(ErrorCode.SIGN_UP_REQUIRED); //이게안된다
        }


        //가입된 유저인 경우(로그인)
        TokenDto t = login(signInRequestDto.getPhoneNum()); //이건잘딤

        return createSignInInfo(t);

    }


    @Transactional
    public SignInResponseDto startWithSignUp(SignUpRequestDto signUpRequestDto){


        //1. 가입시키기

        //1) 가입 정보가 있는 유저인지 확인
        errorService.alreadyExistUser(signUpRequestDto.getPhoneNum());


        //2) 가입진행
        signUp(signUpRequestDto);


        //2. 로그인
        TokenDto t = login(signUpRequestDto.getPhoneNum());


        //3. 필요정보 수집
        return createSignInInfo(t);


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



        // 5. redis에 토큰 저장
        refreshTokenRedisRepository.save(refreshToken);


        return tokenDto;

    }




    //가입
    public URI signUp(SignUpRequestDto signUpRequestDto){

        /*
        save가 안됩니다 -> 근데 id return은 잘되는데 데베에는 없음;;;
         */

        //유저 가입
        UserInfo newUser = signUpRequestDto.toUser(signUpRequestDto.getPhoneNum(), signUpRequestDto.getUserName(), signUpRequestDto.getUserRole());
        UserInfo u = userInfoRepository.save(newUser);


        return createUri(u.getId(), UriTypes.USER );

    }


    //새로 생성되거나 수정된 job의 id를 포함한 URI만들기
    public URI createUri(Long id, UriTypes uriTypes){
        URI uri = UriComponentsBuilder.newInstance()
//                .scheme("https")
//                .host("m-crew.iptime.org")
//                .port(8001)
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/api/"+ uriTypes.getTypeName() + "/" + id)
                .build()
                .toUri(); //UriComponents into URI

        return uri;

    }


    //로그인된 유저에게 반환해주어야 하는 정보들
    @Transactional
    public SignInResponseDto createSignInInfo(TokenDto tokenDto){

        AccessTokenInfo accessTokenInfo = AccessTokenInfo.builder()
                .accessToken(tokenDto.getAccessToken())
                .grantType(tokenDto.getGrantType())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .build();


        UserInfo u = userInfoRepository.findById(tokenDto.getUserId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));


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
                Classes c = classRepository.findById(classId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

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






        UserOnboardInfo userOnboardInfo = UserOnboardInfo.builder()
                .userId(u.getId())
                .userName(u.getUserName())
                .userRole(u.getUserRole())
                .classInfo(classOnboardInfos)
                .build();


        return SignInResponseDto.builder()
                .tokenInfo(accessTokenInfo)
                .userInfo(userOnboardInfo)
                .build();
    }



    @Transactional
    public void logout(HttpServletRequest request) {

        // 1. Request Header 에서 access token 빼기
        String accessToken = tokenProvider.resolveToken(request);

        Long userId = getUserIdFromAccessToken(accessToken);

        //logout token의 TTL은 access token의 남은 기간동안 유지되어야 함
        long remainAccessTokenExpiration = tokenProvider.getRemainExpiration(accessToken);


        //redis에 존재하는 refreshToken 삭제
        refreshTokenRedisRepository.deleteById(userId.toString());


        //logout token를 redis에 저장 (이후 로그아웃된 유저의 AccessToken으로 접근 방지)
        logoutAccessTokenRedisRepository.save(
                LogoutAccessToken.of(accessToken, userId, remainAccessTokenExpiration));
    }



    public Long getUserIdFromAccessToken(String accessToken){

        // access token 유효성 검사
        if (!tokenProvider.validateToken(accessToken)) {
            throw new RuntimeException("Access Token 이 유효하지 않습니다.");
        }

        // access token으로부터 userId 가져오기
        String userId = tokenProvider.getAuthentication(accessToken).getName();


        return Long.parseLong(userId);

    }



}
