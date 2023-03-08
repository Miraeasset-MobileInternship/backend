package miraeassetmobile.backend.service.auth;

import lombok.RequiredArgsConstructor;
import miraeassetmobile.backend.config.security.jwt.TokenProvider;
import miraeassetmobile.backend.domain.dto.auth.*;
import miraeassetmobile.backend.domain.dto.auth.token.RefreshToken;
import miraeassetmobile.backend.domain.dto.auth.token.TokenDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.redis.RefreshTokenRedisRepository;
import miraeassetmobile.backend.service.ErrorService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.domain.enums.UserTypes;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {


    AuthenticationManagerBuilder authenticationManagerBuilder;
    ErrorService errorService;

    UserRepository userRepository;

    TokenProvider tokenProvider;
    RefreshTokenRedisRepository refreshTokenRedisRepository;
    ClassRepository classRepository;

    StudentRepository studentRepository;



    //2가지 리턴 경우의 수가 존재함..
    public Object getStart(SignInRequestDto signInRequestDto){
//        가입안된 유저임 -> 303 SEE OTHER return
        if(!userRepository.existsByPhoneNum(signInRequestDto.getPhoneNumber())){
            //핸드폰 번호 포함-가입을 다시 진행하라는 의미
            return ResponseEntity.accepted().body(signInRequestDto.getPhoneNumber());
        }


        //가입된 유저임(로그인)
        TokenDto t = login(signInRequestDto.getPhoneNumber());

        return createSignInInfo(t);


    }



    public SignInResponseDto startWithSignUp(SignUpRequestDto signUpRequestDto){


        //1. 가입시키기
        System.out.println(1);
        System.out.println(signUpRequestDto.getPhoneNumber());
        System.out.println(signUpRequestDto.getUserName());
        System.out.println(signUpRequestDto.getUserRole());

        System.out.println(11);
        //1) 가입 정보가 있는 유저인지 확인
        errorService.alreadyExistUser(signUpRequestDto.getPhoneNumber());


        System.out.println(2);

        //2) 가입진행
//        signUp(signUpRequestDto);


        System.out.println(3);

        //2. 로그인
//        TokenDto t = login(signUpRequestDto.getPhoneNumber());

        System.out.println(4);

        //3. 필요정보 수집
//        return createSignInInfo(t);

        return null;
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
    public TokenDto login(String phoneNumber){

        //1. 다른 방식으로 인증했으므로 비밀번호 필요 없음
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber,null);

        //2. 유효한 유저인지 검증
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // 3. token 생성
        TokenDto tokenDto = tokenProvider.generateToken(authentication);
        // 4. refresh token 생성 ( database 및 redis 저장을 위한 refresh token )
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
    public void signUp(SignUpRequestDto signUpRequestDto){

        //유저 가입
        UserInfo newUser = signUpRequestDto.toUser(signUpRequestDto.getPhoneNumber(), signUpRequestDto.getUserName(), signUpRequestDto.getUserRole());
        UserInfo u = userRepository.save(newUser);

//        return createUri(t.getId(), UriTypes.TEACHER);

    }


    //로그인된 유저에게 반환해주어야 하는 정보들
    public SignInResponseDto createSignInInfo(TokenDto tokenDto){

        AccessTokenInfo accessTokenInfo = AccessTokenInfo.builder()
                .accessToken(tokenDto.getAccessToken())
                .grantType(tokenDto.getGrantType())
                .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                .build();


        UserInfo u = userRepository.findById(tokenDto.getUserId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));


        List<ClassOnboardInfo> classOnboardInfos = new ArrayList<>();

        if(u.getRole().equals(UserTypes.TEACHER.getTypeName())){
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
                .name(u.getName())
                .role(u.getRole())
                .classInfo(classOnboardInfos)
                .build();


        return SignInResponseDto.builder()
                .tokenInfo(accessTokenInfo)
                .userInfo(userOnboardInfo)
                .build();
    }






}
