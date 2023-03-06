package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.classes.ClassAccountResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import org.springframework.stereotype.Service;

@Service
public class ClassService {

    //에러서비스
    ErrorService errorService;


    //레포
    ClassRepository classRepository;


    ClassService(ClassRepository classRepository, ErrorService errorService){

        this.classRepository = classRepository;
        this.errorService = errorService;
    }


    //학급 국고 정보 조회
    public ClassAccountResponseDto getClassAccountInfo(Long classId){


        //학급조회
        Classes classInfo = classRepository.findById(classId).orElseThrow(()-> new NotExistException(ErrorCode.NOT_EXIST_CLASS));


        return (ClassAccountResponseDto.builder()
                .classId(classInfo.getId())
                .classTitle(classInfo.getTitle())
                .classCurrency(classInfo.getCurrency())
                .classMoney(classInfo.getMoney())
                .build());

    }




}
