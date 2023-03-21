package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.StatusResponse;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.repository.*;
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
    TransactionCategoryRepository transactionCategoryRepository;


    public ResponseService(TransactionCategoryRepository transactionCategoryRepository,UserInfoRepository userInfoRepository, ClassRepository classRepository, JobRepository jobRepository, StudentRepository studentRepository){
        this.userInfoRepository = userInfoRepository;
        this.classRepository =classRepository;
        this.jobRepository =jobRepository;
        this.studentRepository =studentRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
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
            throw new ServiceException(ErrorCode.NOT_EXIST_CLASS);
        }
    }

    public void isExistUser(Long userId){
        if(!userInfoRepository.existsById(userId)){
            throw new ServiceException(ErrorCode.NOT_EXIST_USER);
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
            throw new ServiceException(ErrorCode.NOT_EXIST_JOB);
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


    public void isExistStudent(Long studentId){

        if(!studentRepository.existsById(studentId)){
            throw new ServiceException(ErrorCode.NOT_EXIST_STUDENT);
        }
    }



    //잔고 부족 송금 불가
    public void unavailableTransfer(Long studentId, int transferMoney){
        if(studentRepository.findById(studentId).get().getMoney() < transferMoney){ //출금하려는 금액이 계좌 잔고보다 큰경우
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_TRANSFER_MONEY); // 잔고부족으로 출금 불가
        }
    }

    //잔고 부족 송금 불가
    public void unavailablePay(Long classId, int transferMoney){
        if(classRepository.findById(classId).get().getMoney() < transferMoney){ //출금하려는 금액이 계좌 잔고보다 큰경우
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_PAY_MONEY); // 잔고부족으로 출금 불가
        }
    }


    //직업이 학생 계좌 출금(이체)권한을 가진 직업인가
    public void unavailableJobTransfer(Long jobId){
        if(!jobRepository.findById(jobId).get().isWithdrawStudent()){
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_JOB_TRANSFER);
        }
    }


    //해당 직업이 국고 출금(송금) 권한을 가진 직업인가
    public void unavailableJobPay(Long jobId){
        if(!jobRepository.findById(jobId).get().isWithdrawClass()){
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_JOB_PAY);
        }
    }

    public void alreadyExistUser(String phoneNumber){
        if(userInfoRepository.existsByPhoneNum(phoneNumber)){
            throw new ServiceException(ErrorCode.ALREADY_EXIST_USER);
        }


    }

        //같은 년도에 같은 학교에 같은 반, 학년에 반이 생성되었음
    public void isExistClassWithSameInfo(String schoolName, int grade, int classNumber, String year){
        if(!classRepository.findSameClassInYear(schoolName,grade,classNumber,year).isEmpty()){
            throw new ServiceException(ErrorCode.ALREADY_EXIST_CLASS_SAME_YEAR);
        }
    }


    public void isExistClassWithSameName(String schoolName, String title){
        if(classRepository.existsBySchoolNameAndTitle(schoolName, title)){
            throw new ServiceException(ErrorCode.ALREADY_EXIST_CLASS_SAME_NAME);
        }
    }


    // 0원 이하로 거래 불가
    public void unavailableTransferOrPayZero(int transferMoney){
        if(transferMoney <= 0){ //0원이하 불가능
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_TRANSFER_ZERO); //0원 이하로 거래 불가
        }
    }

    public void wrongTransactionCategoryForTransfer(Long categoryId){
        if(!transactionCategoryRepository.existsByTransferTrueAndId(categoryId)){
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_TRANSFER_TAG);
        }
    }

    public void wrongTransactionCategoryForPay(Long categoryId){
        if(!transactionCategoryRepository.existsByPayTrueAndId(categoryId)){
            throw new ServiceException(ErrorCode.UNAVAILABLE_ACTION_PAY_TAG);
        }
    }


}
