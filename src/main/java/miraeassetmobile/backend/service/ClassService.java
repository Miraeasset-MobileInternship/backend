package miraeassetmobile.backend.service;


import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.classes.*;
import miraeassetmobile.backend.domain.dto.users.ClassEnterStudentResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import miraeassetmobile.backend.repository.redis.ClassInvitationCodeRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.time.LocalDate;
import java.util.Random;

@Service
public class ClassService {




    //레포
    ClassRepository classRepository;
    UserInfoRepository userInfoRepository;
    StudentRepository studentRepository;
    ClassInvitationCodeRedisRepository classInvitationCodeRedisRepository;
    ResponseService responseService;


    ClassService(StudentRepository studentRepository, ResponseService responseService,ClassInvitationCodeRedisRepository classInvitationCodeRedisRepository, UserInfoRepository userInfoRepository, ClassRepository classRepository){
        this.responseService=responseService;
        this.classRepository = classRepository;
        this.userInfoRepository=userInfoRepository;
        this.classInvitationCodeRedisRepository =classInvitationCodeRedisRepository;
        this.studentRepository = studentRepository;
    }


    //학급 국고 정보 조회
    public BanklassResponseEntity getClassAccountInfo(Long classId){


        //학급조회
        Classes c = classRepository.findById(classId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));

        UserInfo teacher = userInfoRepository.findById(c.getTeacherId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));

        return (
                responseService.successHandler(
                ClassAccountResponseDto.builder()
                .classId(c.getId())
                .classTitle(c.getTitle())
                .classCurrency(c.getCurrency())
                .classMoney(c.getMoney())
                .teacherName(teacher.getUserName())
                .classGrade(c.getGrade())
                .classNumber(c.getClassNum())
                .schoolName(c.getSchoolName())
                .build())
        );

    }


    @Transactional
    //신규학급 등록
    public BanklassResponseEntity createClass(ClassCreateRequestDto classCreateRequestDto){

        LocalDate today = LocalDate.now();
        int year = today.getYear();

        //해당 년도에 이 학교/학년/반에서 생성된 학급이 존재하는 경우
        responseService.isExistClassWithSameInfo(classCreateRequestDto.getSchoolName(), classCreateRequestDto.getGrade(),classCreateRequestDto.getClassNumber(), Integer.toString(year));

        //해당 학교에 같은 이름으로 등록된 나라가 있을 경우
        responseService.isExistClassWithSameName(classCreateRequestDto.getSchoolName(), classCreateRequestDto.getTitle());

        try {

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


        return responseService.successHandler(
                CreatedUriDto.builder()
                        .url(responseService.createUri(c.getId(), UriTypes.CLASS))
                        .status("created")
                        .build()
            ); //반 신규 생성

        }catch(Exception e){ //저장 과정에서 에러난 경우
            throw new ServiceException(ErrorCode.NOT_SAVE_CLASS);
        }
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


    public BanklassResponseEntity getClassInvitationCode(Long classId){



        ClassInvitationCode classInvitationCode = classInvitationCodeRedisRepository.findById(classId.toString())
                .orElseGet(() -> reissueInvitationCode(classId)); // 만료되었으면 재생성해서 돌려줌



        return responseService.successHandler(
                ClassInvitationCodeResponseDto.builder()
                .classId(classId)
                .invitationCode(classInvitationCode.getInvitationCode())
                .build()
                );
    }





    @Transactional
    public ClassInvitationCode reissueInvitationCode(Long classId){

        //초대 코드 생성

        long expiration = 60 * 60 * 24 * 7; //유효기간 일주일

        try {

            ClassInvitationCode code = ClassInvitationCode.builder()
                    .id(classId.toString()) //새로 생성된 클래스
                    .invitationCode(createInvitationCode())
                    .expiration(expiration)
                    .build();

            //redis에 유효기간 일주일로 저장
            classInvitationCodeRedisRepository.save(code);


            return code;

        }catch(Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_CODE);
        }

    }


    public BanklassResponseEntity checkInvitationCode(String invitationCode){


        ClassInvitationCode classInvitationCode = classInvitationCodeRedisRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new ServiceException(ErrorCode.INCORRECT_CODE)); //올바르지 않거나 만료된 인증번호


        Classes c = classRepository.findById(Long.parseLong(classInvitationCode.getId()))
                .orElseThrow(()-> new ServiceException(ErrorCode.NOT_EXIST_CLASS));


        UserInfo teacher = userInfoRepository.findById(c.getTeacherId()).orElseThrow(()-> new ServiceException(ErrorCode.NOT_EXIST_USER));

        return responseService.successHandler(
                ClassValidInvitationResponseDto.builder()
                .classId(c.getId())
                .title(c.getTitle())
                .schoolName(c.getSchoolName())
                .grade(c.getGrade())
                .classNumber(c.getClassNum())
                .teacherName(teacher.getUserName())
                .build()
        );

    }


    public BanklassResponseEntity getClassCurrency(Long classId){


        Classes c = classRepository.findById(classId).orElseThrow(()-> new ServiceException(ErrorCode.NOT_EXIST_CLASS));

        return responseService.successHandler(
                ClassCurrencyResponseDto.builder()
                        .classId(classId)
                        .currency(c.getCurrency())
                        .build()
        );

    }



    public BanklassResponseEntity getInfoToEnterClass(Long classId, Long userId){


        responseService.isExistClass(classId);
        responseService.isExistUser(userId);

        Student student = studentRepository.findByClassIdAndUserId(classId,userId)
                .orElseThrow(()-> new ServiceException(ErrorCode.UNAVAILABLE_ACTION_NOT_INCLUDED_STUDENT));//속한 학생이 아닌 경우


        return responseService.successHandler(

                ClassEnterStudentResponseDto.builder()
                        .studentId(student.getId())
                        .classId(student.getClassId())
                        .build()

        );



    }

}
