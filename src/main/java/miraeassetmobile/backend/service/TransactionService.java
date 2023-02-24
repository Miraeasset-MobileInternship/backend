package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.transactions.StudentTransactionDataDto;
import miraeassetmobile.backend.domain.dto.transactions.StudentTransactionResponseDto;
import miraeassetmobile.backend.domain.dto.transactions.TransactionCategoryDto;
import miraeassetmobile.backend.domain.dto.transactions.TransferMoneyRequestDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.TransactionCategory;
import miraeassetmobile.backend.domain.entity.TransactionData;
import miraeassetmobile.backend.domain.entity.enums.TransactionFromTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.error.exception.UnavailableException;
import miraeassetmobile.backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import static miraeassetmobile.backend.domain.entity.enums.TransactionFromTypes.*;


@Service
public class TransactionService {

    TransactionDataRepository transactionDataRepository;
    TransactionCategoryRepository transactionCategoryRepository;
    StudentRepository studentRepository;
    ClassRepository classRepository;
    JobRepository jobRepository;


    TransactionService(JobRepository jobRepository, ClassRepository classRepository, TransactionCategoryRepository transactionCategoryRepository, TransactionDataRepository transactionDataRepository, StudentRepository studentRepository){
        this.studentRepository=studentRepository;
        this.transactionCategoryRepository=transactionCategoryRepository;
        this.transactionDataRepository=transactionDataRepository;
        this.classRepository = classRepository;
        this.jobRepository = jobRepository;
    }



    //학생별 거래내역 조회 -> 입출금 따로 조회 가능으로 대체되었지만 일단 임시로 살려둠(m-crew 버전에는 없음
//    public StudentTransactionResponseDto getStudentTransactionData(Long studentId, int page){
//
//        int pageSize = 10;
//
//
//        Pageable pageable = PageRequest.of(page,pageSize, Sort.by("createTimestamp").descending()); //최신순
//        Page<TransactionData> transactions = transactionDataRepository.findByStudentId(studentId, pageable);
//
//
//        int totalData = transactionDataRepository.countByStudentId(studentId);
//
//
//        int maxPage = (int) Math.ceil(totalData/(double)pageSize) -1; //요청가능한 마지막 페이지
//
//
//        List<StudentTransactionDataDto> studentTransactionDatas = new ArrayList<>();
//
//        for (TransactionData t: transactions) {
//
//
//            boolean isDeposit = t.getFrom().equals("class"); //돈의 출처가 학생이면 출금
//
//
//            StudentTransactionDataDto transaction = StudentTransactionDataDto.builder()
//                    .transactionId(t.getId())
//                    .category(transactionCategoryRepository.findById(t.getCategoryId()).get().getTitle())
//                    .detail(t.getDetail())
//                    .isDeposit(isDeposit)
//                    .transactionMoney(t.getMoney())
//                    .transactionDate(t.getCreateTimestamp().toLocalDateTime().toLocalDate())
//                    .build();
//
//            studentTransactionDatas.add(transaction);
//
//        }
//
//        return StudentTransactionResponseDto.builder()
//                .currentPage(page)
//                .totalData(totalData)
//                .maxPage(maxPage)
//                .studentTransactionData(studentTransactionDatas)
//                .build();
//
//    }


    //학생별 거래내역 조회 (입출금 분리)
    public StudentTransactionResponseDto getStudentTransactionDataWithType(Long studentId, int page, String type){

        int pageSize = 10;
        int totalData = 0;
        Page<TransactionData> transactions;

        if(type.equals("all")){

            Pageable pageable = PageRequest.of(page,pageSize, Sort.by("createTimestamp").descending()); //최신순
            transactions = transactionDataRepository.findByStudentId(studentId, pageable);
            totalData = transactionDataRepository.countByStudentId(studentId);

        }else{

            TransactionFromTypes searchType = (type.equals("deposit") ? (CLASS):(STUDENT));

            Pageable pageable = PageRequest.of(page,pageSize, Sort.by("createTimestamp").descending()); //최신순
            transactions = transactionDataRepository.findByStudentIdAndFrom(studentId, searchType.getTypeName(), pageable);
            totalData = transactionDataRepository.countByStudentIdAndAndFrom(studentId,searchType.getTypeName());
        }

        int maxPage = (int) Math.ceil(totalData/(double)pageSize) -1; //요청가능한 마지막 페이지


        List<StudentTransactionDataDto> studentTransactionDatas = new ArrayList<>();

        for (TransactionData t: transactions) {


            boolean isDeposit = t.getFrom().equals("class"); //돈의 출처가 학생이면 출금


            StudentTransactionDataDto transaction = StudentTransactionDataDto.builder()
                    .transactionId(t.getId())
                    .category(transactionCategoryRepository.findById(t.getCategoryId()).get().getTitle())
                    .detail(t.getDetail())
                    .isDeposit(isDeposit)
                    .transactionMoney(t.getMoney())
                    .transactionDate(t.getCreateTimestamp().toLocalDateTime().toLocalDate())
                    .build();

            studentTransactionDatas.add(transaction);

        }

        return StudentTransactionResponseDto.builder()
                .currentPage(page)
                .totalData(totalData)
                .maxPage(maxPage)
                .studentTransactionData(studentTransactionDatas)
                .build();

    }


    public List<TransactionCategoryDto> getCategoryList(){

        List<TransactionCategory> categories = transactionCategoryRepository.findByChangeableTrue();

        List<TransactionCategoryDto> result = new ArrayList<>();

        for (TransactionCategory t: categories) {

            result.add(TransactionCategoryDto.builder()
                    .categoryId(t.getId())
                    .categoryTitle(t.getTitle())
                    .build());

        }

        return result;
    }




    public URI transferMoney(TransferMoneyRequestDto transferMoneyRequestDto){

                /*
        1. 학생의 계좌의 잔고를 확인함
            -> 부족하면 에러 발생시켜야함
        2. 학생 계좌에서 돈을 출금함(minus)
        3. 국고 계좌에 돈을 추가함(plus)
        4. transfer_data table에 데이터를 추가함
         */


        //존재하는 학생들인가
        isExistStudent(transferMoneyRequestDto.getStudentId());
        isExistStudent(transferMoneyRequestDto.getManagerId());


        Student manager = studentRepository.findById(transferMoneyRequestDto.getManagerId()).get();
        Student student = studentRepository.findById(transferMoneyRequestDto.getStudentId()).get();


        //0. "매니저"가 송금 권한이 있는 (직업의) 학생인가
        unavailableJobTransfer(manager.getJobId());


        //1. 학생의 계좌의 잔고를 확인한다.
        //송금하려는 금액이 계좌에 충분히 있는지 검사
        unavailableTransfer(transferMoneyRequestDto.getStudentId(), transferMoneyRequestDto.getMoney());

        //2. 학생 계좌 잔고를 수정한다
        updateTransferStudentMoney(student.getId(), transferMoneyRequestDto.getMoney());


        //3.국고 계좌에 돈을 추가함(plus)
        updateTransferClassMoney(student.getClassId(), transferMoneyRequestDto.getMoney());

        //4.transfer_data table에 데이터 추가


        TransactionData transactionData = transactionDataRepository.save(TransactionData.builder()
                .money(transferMoneyRequestDto.getMoney())
                .managerId(manager.getId())
                .managerJobId(manager.getJobId())
                .studentId(student.getId())
                .studentJobId(student.getJobId())
                .classId(student.getClassId())
                .categoryId(transferMoneyRequestDto.getCategoryId())
                .detail(transferMoneyRequestDto.getDetail())
                .from(STUDENT.getTypeName()) //이체하기 (학생 잔고에서 뽑아오는 것) FROM 학생
                .build());

        return(createUri(transactionData.getId(), "transaction"));


    }


    public URI createUri(Long id, String newOne){
        URI uri = UriComponentsBuilder.newInstance()
//                .scheme("https")
//                .host("m-crew.iptime.org")
//                .port(8001)
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/api/"+ newOne + "/" + id)
                .build()
                .toUri(); //UriComponents into URI

        return uri;

    }

    public void updateTransferStudentMoney(Long studentId, int transferMoney){


        Student student = studentRepository.findById(studentId).get();

        //객체의 돈을 변경하여 새로운 객체를 생성
        Student updateStudent = student.updateMoney(student.getMoney() - transferMoney); //보유금액 - 출금금액

        studentRepository.save(updateStudent);
    }

    public void updateTransferClassMoney(Long classId, int transferMoney){

//        Student student = studentRepository.findById(studentId).get();

        //속해있는 학급 구하기
        Classes studentClass = classRepository.findById(classId).get();

        //객체의 돈을 변경하여 새로운 객체를 생성
        Classes updateClass = studentClass.updateMoney(studentClass.getMoney() + transferMoney); //보유금액 - 출금금액

        classRepository.save(updateClass);

    }






    //잔고 부족 송금 불가
    public void unavailableTransfer(Long studentId, int transferMoney){
        if(studentRepository.findById(studentId).get().getMoney() < transferMoney){ //출금하려는 금액이 계좌 잔고보다 큰경우
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_TRANSFER_MONEY); // 잔고부족으로 출금 불가
        }
    }

    //직업이 학생 계좌 출금(이체)권한을 가진 직업인가
    public void unavailableJobTransfer(Long jobId){
        if(!jobRepository.findById(jobId).get().isWithdrawStudent()){
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_JOB_TRANSFER);
        }
    }


    //존재하는 학생인가
    public void isExistStudent(Long studentId){

        if(!studentRepository.existsById(studentId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_STUDENT);
        }
    }

}
