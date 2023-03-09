package miraeassetmobile.backend.service;

import miraeassetmobile.backend.error.exception.*;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class ErrorService {


    ClassRepository classRepository;
    JobRepository jobRepository;
    StudentRepository studentRepository;
    UserInfoRepository userInfoRepository;


    public ErrorService(UserInfoRepository userInfoRepository, ClassRepository classRepository, JobRepository jobRepository, StudentRepository studentRepository){
        this.userInfoRepository = userInfoRepository;
        this.classRepository =classRepository;
        this.jobRepository =jobRepository;
        this.studentRepository =studentRepository;
    }



    public void isExistClass(Long classId){
        if(!classRepository.existsById(classId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_CLASS);
        }
    }


    //필수직업 삭제 불가능
    public void unavailableJobDelete(Long jobId){
        if(jobRepository.findById(jobId).get().getClassId() == 1){ //master job인 경우
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_DELETE_JOB); // 삭제 불가능한 것을 삭제하려고 한다.
        }
    }

    public void isExistJob(Long jobId){

        if(!jobRepository.existsById(jobId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_JOB);
        }
    }

    public void isExistStudent(Long studentId){

        if(!studentRepository.existsById(studentId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_STUDENT);
        }
    }



    public void validateJobNameInClass(Long classId, String title){

        if(jobRepository.existsByClassIdAndTitle(classId, title)){ //해당 학급에 같은 이름의 직업이 이미 존재함
            throw new AlreadyExistException(ErrorCode.ALREADY_EXIST_JOB);
        }

    }



    //잔고 부족 송금 불가
    public void unavailableTransfer(Long studentId, int transferMoney){
        if(studentRepository.findById(studentId).get().getMoney() < transferMoney){ //출금하려는 금액이 계좌 잔고보다 큰경우
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_TRANSFER_MONEY); // 잔고부족으로 출금 불가
        }
    }

    //잔고 부족 송금 불가
    public void unavailablePay(Long classId, int transferMoney){
        if(classRepository.findById(classId).get().getMoney() < transferMoney){ //출금하려는 금액이 계좌 잔고보다 큰경우
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_PAY_MONEY); // 잔고부족으로 출금 불가
        }
    }


    //직업이 학생 계좌 출금(이체)권한을 가진 직업인가
    public void unavailableJobTransfer(Long jobId){
        if(!jobRepository.findById(jobId).get().isWithdrawStudent()){
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_JOB_TRANSFER);
        }
    }


    //해당 직업이 국고 출금(송금) 권한을 가진 직업인가
    public void unavailableJobPay(Long jobId){
        if(!jobRepository.findById(jobId).get().isWithdrawClass()){
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_JOB_PAY);
        }
    }


    //직업 한 학급 당 50개 이상 등록 불가
    public void unavailableJobRegister(Long classId){

        int total = classRepository.countById(classId) + classRepository.countById(1L);

        if(!classId.equals(1L) && total >= 50 ){
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_TOO_MANY_JOBS);
        }

    }



    //주식 시세 관련 API 서버에서 발생할 수 있는 에러


    public void errorFromExternalServerNoResult(){ //외부 서버 에러 -> 결과가 존재하지 않음(조회 코드가 틀려서 결과가 없음)
            throw new ExternalErrorException(ErrorCode.EXTERNAL_SERVER_NO_RESULT_ERROR);

    }


    public void errorFromExternalServer(){ //외부 서버 에러(공통)
        throw new ExternalErrorException(ErrorCode.EXTERNAL_SERVER_ERROR);
    }





    public void alreadyExistUser(String phoneNumber){
        if(userInfoRepository.existsByPhoneNum(phoneNumber)){
            throw new AlreadyExistException(ErrorCode.ALREADY_EXIST_USER);
        }


    }



}
