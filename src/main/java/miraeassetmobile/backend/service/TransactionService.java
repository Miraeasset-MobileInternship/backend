package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.students.StudentJobDto;
import miraeassetmobile.backend.domain.dto.transactions.*;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.TransactionCategory;
import miraeassetmobile.backend.domain.entity.TransactionData;
import miraeassetmobile.backend.domain.entity.enums.TransactionFromTypes;
import miraeassetmobile.backend.domain.entity.enums.UriTypes;
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


    ErrorService errorService;

    TransactionDataRepository transactionDataRepository;
    TransactionCategoryRepository transactionCategoryRepository;
    StudentRepository studentRepository;
    ClassRepository classRepository;
    JobRepository jobRepository;


    TransactionService(ErrorService errorService, JobRepository jobRepository, ClassRepository classRepository, TransactionCategoryRepository transactionCategoryRepository, TransactionDataRepository transactionDataRepository, StudentRepository studentRepository){
        this.studentRepository=studentRepository;
        this.transactionCategoryRepository=transactionCategoryRepository;
        this.transactionDataRepository=transactionDataRepository;
        this.classRepository = classRepository;
        this.jobRepository = jobRepository;
        this.errorService =errorService;
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


        //존재하는 학생인지
        errorService.isExistStudent(studentId);


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






    //학급별 "국고" 거래내역 조회 (입출금 분리)
    public ClassTransactionResponseDto getClassTransactionDataWithType(Long classId, int page, String type){


        //존재하는학급인지
        errorService.isExistClass(classId);



        int pageSize = 10;
        int totalData = 0;
        Page<TransactionData> transactions;

        if(type.equals("all")){

            Pageable pageable = PageRequest.of(page,pageSize, Sort.by("createTimestamp").descending()); //최신순
            transactions = transactionDataRepository.findByClassId(classId, pageable);
            totalData = transactionDataRepository.countByClassId(classId);

        }else{

            //국고 기준으로는 학생계좌에서 출금된게 입금임! **주의**
            TransactionFromTypes searchType = (type.equals("deposit") ? (STUDENT):(CLASS));

            Pageable pageable = PageRequest.of(page,pageSize, Sort.by("createTimestamp").descending()); //최신순
            transactions = transactionDataRepository.findByClassIdAndFrom(classId, searchType.getTypeName(), pageable);
            totalData = transactionDataRepository.countByClassIdAndAndFrom(classId,searchType.getTypeName());
        }

        int maxPage = (int) Math.ceil(totalData/(double)pageSize) -1; //요청가능한 마지막 페이지


        List<ClassTransactionDataDto> classTransactionDatas = new ArrayList<>();

        for (TransactionData t: transactions) {


            boolean isDeposit = t.getFrom().equals("student"); //돈의 출처가 "학급(국고)"이면 출금

            //**주의**
            // JobId를 t에서 가져와야 해당 거래 당시의 직업으로 출력 가능( 학생에서 가져오면 변경된 직업으로 나옴)
            StudentJobDto managerDto = getStudentJobDto(t.getManagerId(), t.getManagerJobId());
            StudentJobDto studentDto = getStudentJobDto(t.getStudentId(), t.getStudentJobId());

            ClassTransactionDataDto transaction = ClassTransactionDataDto.builder()
                    .transactionId(t.getId())
                    .transactionDate(t.getCreateTimestamp().toLocalDateTime().toLocalDate())
                    .category(transactionCategoryRepository.findById(t.getCategoryId()).get().getTitle())
                    .detail(t.getDetail())
                    .isDeposit(isDeposit)
                    .transactionMoney(t.getMoney())
                    .manager(managerDto)
                    .student(studentDto)
                    .build();

            classTransactionDatas.add(transaction);

        }

        return ClassTransactionResponseDto.builder()
                .currentPage(page)
                .maxPage(maxPage)
                .totalData(totalData)
                .classTransactionData(classTransactionDatas)
                .build();

    }


    //student Id를 주면 stduentjobDto를 반환해주는 함수
    public StudentJobDto getStudentJobDto(Long studentId, Long studentJobId){


        Student s = studentRepository.findById(studentId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

        return (StudentJobDto.builder()
                .id(studentId)
                .number(s.getNumber())
                .studentName(s.getName())
                .jobId(studentJobId) //주의 : student를 찾아서 걔의 jobId를 가져오면 직업이 변경되면 데이터 로그도 변경됨!! 로그는 그 당시 직업을 저장
                .jobTitle(jobRepository.findById(studentJobId).get().getTitle())
                .build());
    }



    public List<TransactionCategoryDto> getCategoryList(String type){



        List<TransactionCategory> categories;

        if(type.equals("transfer")){
            categories = transactionCategoryRepository.findByTransferTrue();
        }else{//pay
            categories = transactionCategoryRepository.findByPayTrue();
        }



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


        Student manager = studentRepository.findById(transferMoneyRequestDto.getManagerId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));
        Student student = studentRepository.findById(transferMoneyRequestDto.getStudentId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));


        //0. "매니저"가 송금 권한이 있는 (직업의) 학생인가
        errorService.unavailableJobTransfer(manager.getJobId());


        //1. 학생의 계좌의 잔고를 확인한다.
        //송금하려는 금액이 계좌에 충분히 있는지 검사
        errorService.unavailableTransfer(transferMoneyRequestDto.getStudentId(), transferMoneyRequestDto.getMoney());

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

        return(createUri(transactionData.getId(), UriTypes.TRANSACTION));


    }



    public URI payMoney(TransferMoneyRequestDto transferMoneyRequestDto){

        /*
        1. 국고의 잔고를 확인함
            -> 부족하면 에러 발생시켜야함
        2. 국고 계좌에서 돈을 출금함(minus)
        3. 학생 계좌에 돈을 추가함(plus)
        4. transfer_data table에 데이터를 추가함
         */

        Student manager = studentRepository.findById(transferMoneyRequestDto.getManagerId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));
        Student student = studentRepository.findById(transferMoneyRequestDto.getStudentId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));


        //0. "매니저"가 권한이 있는 (직업의) 학생인가
        errorService.unavailableJobPay(manager.getJobId());


        //1. 국고의 잔고를 확인한다.
        //송금하려는 금액이 계좌에 충분히 있는지 검사
        errorService.unavailablePay(student.getClassId(), transferMoneyRequestDto.getMoney());

        //2. 국고 잔고를 수정한다
        updatePayClassMoney(student.getClassId(), transferMoneyRequestDto.getMoney());



        //3.학생 계좌에 돈을 추가함(plus)
        updatePayStudentMoney(student.getId(), transferMoneyRequestDto.getMoney());

        //4.transfer_data table에 데이터 추가


        TransactionData transactionData = transactionDataRepository.save(TransactionData.builder()
                .money(transferMoneyRequestDto.getMoney())
                .managerId(manager.getId())
                .managerJobId(manager.getJobId()) //현재 가지고 있는 직업이 저장
                .studentId(student.getId())
                .studentJobId(student.getJobId())
                .classId(student.getClassId())
                .categoryId(transferMoneyRequestDto.getCategoryId())
                .detail(transferMoneyRequestDto.getDetail())
                .from(CLASS.getTypeName()) //지급하기 (국고 잔고에서 뽑아오는 것) FROM class
                .build());

        return(createUri(transactionData.getId(), UriTypes.TRANSACTION));


    }



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

    public void updateTransferStudentMoney(Long studentId, int transferMoney){


        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

        //객체의 돈을 변경하여 새로운 객체를 생성
        Student updateStudent = student.updateMoney(student.getMoney() - transferMoney); //보유금액 - 출금금액

        studentRepository.save(updateStudent);
    }

    public void updateTransferClassMoney(Long classId, int transferMoney){

        //속해있는 학급 구하기
        Classes studentClass = classRepository.findById(classId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

        //객체의 돈을 변경하여 새로운 객체를 생성
        Classes updateClass = studentClass.updateMoney(studentClass.getMoney() + transferMoney); //보유금액 + 출금금액

        classRepository.save(updateClass);

    }


    public void updatePayClassMoney(Long classId, int transferMoney){


        //속해있는 학급 구하기
        Classes studentClass = classRepository.findById(classId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

        //객체의 돈을 변경하여 새로운 객체를 생성
        Classes updateClass = studentClass.updateMoney(studentClass.getMoney() - transferMoney); //보유금액 - 출금금액 (돈사용)

        classRepository.save(updateClass);

    }

    public void updatePayStudentMoney(Long studentId, int transferMoney){


        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

        //객체의 돈을 변경하여 새로운 객체를 생성
        Student updateStudent = student.updateMoney(student.getMoney() + transferMoney); //보유금액 + 출금금액

        studentRepository.save(updateStudent);
    }


}
