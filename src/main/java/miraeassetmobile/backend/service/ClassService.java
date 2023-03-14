package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.auth.sms.PhoneNumberCode;
import miraeassetmobile.backend.domain.dto.classes.*;
import miraeassetmobile.backend.domain.dto.jobs.JobCreateRequestDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import miraeassetmobile.backend.repository.redis.ClassInvitationCodeRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Random;

@Service
public class ClassService {

    //에러서비스
    ErrorService errorService;


    //레포
    ClassRepository classRepository;
    UserInfoRepository userInfoRepository;
    ClassInvitationCodeRedisRepository classInvitationCodeRedisRepository;

    ClassService(ClassInvitationCodeRedisRepository classInvitationCodeRedisRepository, UserInfoRepository userInfoRepository,ClassRepository classRepository, ErrorService errorService){

        this.classRepository = classRepository;
        this.errorService = errorService;
        this.userInfoRepository=userInfoRepository;
        this.classInvitationCodeRedisRepository =classInvitationCodeRedisRepository;
    }


    //학급 국고 정보 조회
    public ClassAccountResponseDto getClassAccountInfo(Long classId){


        //학급조회
        Classes c = classRepository.findById(classId).orElseThrow(()-> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

        UserInfo teacher = userInfoRepository.findById(c.getTeacherId()).orElseThrow(()->new NotExistException(ErrorCode.NOT_EXSIT_USER));

        return (ClassAccountResponseDto.builder()
                .classId(c.getId())
                .classTitle(c.getTitle())
                .classCurrency(c.getCurrency())
                .classMoney(c.getMoney())
                .teacherName(teacher.getUserName())
                .classGrade(c.getGrade())
                .classNumber(c.getClassNum())
                .schoolName(c.getSchoolName())
                .build());

    }


    @Transactional
    //신규학급 등록
    public URI createClass(ClassCreateRequestDto classCreateRequestDto){

        LocalDate today = LocalDate.now();
        int year = today.getYear();

        //해당 년도에 이 학교/학년/반에서 생성된 학급이 존재하는 경우
        errorService.isExistClassWithSameInfo(classCreateRequestDto.getSchoolName(), classCreateRequestDto.getGrade(),classCreateRequestDto.getClassNumber(), Integer.toString(year));

        //해당 학교에 같은 이름으로 등록된 나라가 있을 경우
        errorService.isExistClassWithSameName(classCreateRequestDto.getSchoolName(), classCreateRequestDto.getTitle());


        //새로운 클래스 동록
        Classes newClass = classCreateRequestDto.toClass(classCreateRequestDto.getTeacher_id(), classCreateRequestDto.getTitle(), classCreateRequestDto.getGrade(), classCreateRequestDto.getClassNumber(), classCreateRequestDto.getCurrency(), classCreateRequestDto.getSchoolName()); //save에서 에러난다


        Classes c = classRepository.save(newClass);



        //초대 코드 생성

        long expiration = 60 * 60 * 24 * 7; //유효기간 일주일

        ClassInvitationCode classCode = ClassInvitationCode.builder()
                .id(c.getId().toString()) //새로 생성된 클래스
                .invitationCode(createInvitationCode())
                .expiration(expiration)
                .build();

        //redis에 유효기간 일주일로 저장
        classInvitationCodeRedisRepository.save(classCode);


        return createUri(c.getId(), UriTypes.CLASS); //등록된 직업에 대해 URI를 같이 반환함

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




    //6자리 랜덤 암호 만들기
    public String createInvitationCode(){

        // 숫자만으로 랜덤번호 생성
        String numList = "abcdefghijklnmopqrstuvwxyzABCDEFGHIJKLNMOPQRSTUVWXYZ";
        int alphaNumLength = numList.length();

        Random random = new Random();

        StringBuffer code = new StringBuffer();
        for (int i = 0; i < 15; i++) { //15자리암호
            code.append(numList.charAt(random.nextInt(alphaNumLength)));
        }

        return code.toString();

    }


    public ClassInvitationCodeResponseDto getClassInvitationCode(Long classId){



        ClassInvitationCode classInvitationCode = classInvitationCodeRedisRepository.findById(classId.toString())
                .orElseGet(() -> reissueInvitationCode(classId)); // 만료되었으면 재생성해서 돌려줌



        return ClassInvitationCodeResponseDto.builder()
                .classId(classId)
                .invitationCode(classInvitationCode.getInvitationCode())
                .build();

    }





    @Transactional
    public ClassInvitationCode reissueInvitationCode(Long classId){

        //초대 코드 생성

        long expiration = 60 * 60 * 24 * 7; //유효기간 일주일


        ClassInvitationCode code = ClassInvitationCode.builder()
                .id(classId.toString()) //새로 생성된 클래스
                .invitationCode(createInvitationCode())
                .expiration(expiration)
                .build();

        //redis에 유효기간 일주일로 저장
        classInvitationCodeRedisRepository.save(code);


        return code;
    }

}
