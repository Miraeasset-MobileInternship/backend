package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.classes.ClassAccountResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class ClassService {

    //에러서비스
    ErrorService errorService;


    //레포
    ClassRepository classRepository;
    UserInfoRepository userInfoRepository;

    ClassService(UserInfoRepository userInfoRepository,ClassRepository classRepository, ErrorService errorService){

        this.classRepository = classRepository;
        this.errorService = errorService;
        this.userInfoRepository=userInfoRepository;
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




}
