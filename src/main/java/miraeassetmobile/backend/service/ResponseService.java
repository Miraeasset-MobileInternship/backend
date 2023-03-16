package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.StatusResponse;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import miraeassetmobile.backend.error.exception.ServiceException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ResponseService {


    ClassRepository classRepository;
    JobRepository jobRepository;
    StudentRepository studentRepository;
    UserInfoRepository userInfoRepository;


    public ResponseService(UserInfoRepository userInfoRepository, ClassRepository classRepository, JobRepository jobRepository, StudentRepository studentRepository){
        this.userInfoRepository = userInfoRepository;
        this.classRepository =classRepository;
        this.jobRepository =jobRepository;
        this.studentRepository =studentRepository;
    }


    public BanklassResponseEntity successHandler(Object jsonObject){

        StatusResponse statusResponse = StatusResponse.builder()
                .status(ErrorCode.SUCCESS.getStatus())
                .message(ErrorCode.SUCCESS.getDetail())
                .build();


        return BanklassResponseEntity.builder()
                        .status(statusResponse)
                        .result(jsonObject)
                        .build();

    }


    public ResponseEntity<BanklassResponseEntity> errorHandler(ErrorCode errorCode){

        StatusResponse statusResponse = StatusResponse.builder()
                .status(errorCode.getStatus())
                .message(errorCode.getDetail())
                .build();


        return ResponseEntity.ok(
                BanklassResponseEntity.builder()
                        .status(statusResponse)
                        .result(new Object())
                        .build()
        );

    }





    //새로 생성되거나 수정된 job의 id를 포함한 URI만들기
    public String createUri(Long id, UriTypes uriTypes){
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

        return uri.toString();

    }




    public void isExistClass(Long classId){
        if(!classRepository.existsById(classId)){
            throw new ServiceException(ErrorCode.NOT_EXIST);
        }
    }

    public void isExistUser(Long userId){
        if(!userInfoRepository.existsById(userId)){
            throw new ServiceException(ErrorCode.NOT_EXIST);
        }
    }

    public void isUserExistInClass(Long classId, Long userId){
        isExistUser(userId);
        isExistClass(classId);

        if(studentRepository.existsByClassIdAndUserId(classId,userId)){
            throw new ServiceException(ErrorCode.ALREADY_EXIST_IN_CLASS);
        }

    }



    //필수직업 삭제 불가능
    public void unavailableJobDelete(Long jobId){
        if(jobRepository.findById(jobId).get().getClassId() == 1){ //master job인 경우
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_DELETE_JOB); // 삭제 불가능한 것을 삭제하려고 한다.
        }
    }

    public void isExistJob(Long jobId){

        if(!jobRepository.existsById(jobId)){
            throw new ServiceException(ErrorCode.NOT_EXIST);
        }
    }

    public void validateJobNameInClass(Long classId, String title){
        //해당 학급에 같은 이름의 직업이 이미 존재함
        // 공통 직업도 함께 처리해줘야함
        if(jobRepository.existsByClassIdAndTitle(classId, title)||jobRepository.existsByClassIdAndTitle(1L, title)){
            throw new ServiceException(ErrorCode.ALREADY_EXIST_JOB_IN_CLASS);
        }

    }

    //    //직업 한 학급 당 50개 이상 등록 불가
    public void unavailableJobRegister(Long classId){

        int total = classRepository.countById(classId) + classRepository.countById(1L);

        if(!classId.equals(1L) && total >= 50 ){
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_TOO_MANY_JOBS);
        }

    }

}
