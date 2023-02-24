package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.classes.ClassAccountResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import org.springframework.stereotype.Service;

@Service
public class ClassService {


    ClassRepository classRepository;


    ClassService(ClassRepository classRepository){
        this.classRepository = classRepository;
    }


    //학급 국고 정보 조회
    public ClassAccountResponseDto getClassAccountInfo(Long classId){

        //존재하는 학급인가
        isExistClass(classId);


        //학급조회
        Classes classInfo = classRepository.findById(classId).get();


        return (ClassAccountResponseDto.builder()
                .classId(classInfo.getId())
                .classTitle(classInfo.getTitle())
                .classCurrency(classInfo.getCurrency())
                .classMoney(classInfo.getMoney())
                .build());

    }


    public void isExistClass(Long classId){
        if(!classRepository.existsById(classId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_CLASS);
        }
    }

}
