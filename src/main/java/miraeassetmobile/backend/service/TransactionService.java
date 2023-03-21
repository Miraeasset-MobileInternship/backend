package miraeassetmobile.backend.service;


import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.students.StudentInfoDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;
import miraeassetmobile.backend.domain.dto.transactions.*;
import miraeassetmobile.backend.domain.entity.*;
import miraeassetmobile.backend.domain.enums.TransactionFromTypes;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static miraeassetmobile.backend.domain.enums.TransactionFromTypes.CLASS;
import static miraeassetmobile.backend.domain.enums.TransactionFromTypes.STUDENT;


@Service
public class TransactionService {


    TransactionDataRepository transactionDataRepository;
    TransactionCategoryRepository transactionCategoryRepository;
    StudentRepository studentRepository;
    ClassRepository classRepository;
    JobRepository jobRepository;
    UserInfoRepository userInfoRepository;

    ResponseService responseService;


    TransactionService(ResponseService responseService,UserInfoRepository userInfoRepository,  JobRepository jobRepository, ClassRepository classRepository, TransactionCategoryRepository transactionCategoryRepository, TransactionDataRepository transactionDataRepository, StudentRepository studentRepository){
        this.studentRepository=studentRepository;
        this.transactionCategoryRepository=transactionCategoryRepository;
        this.transactionDataRepository=transactionDataRepository;
        this.classRepository = classRepository;
        this.jobRepository = jobRepository;
        this.userInfoRepository = userInfoRepository;
        this.responseService = responseService;
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
    public BanklassResponseEntity getStudentTransactionDataWithType(Long studentId, int page, String type){


        //존재하는 학생인지
        responseService.isExistStudent(studentId);


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

        return responseService.successHandler(StudentTransactionResponseDto.builder()
                .currentPage(page)
                .totalData(totalData)
                .maxPage(maxPage)
                .studentTransactionData(studentTransactionDatas)
                .build());

    }






    //학급별 "국고" 거래내역 조회 (입출금 분리)
    public BanklassResponseEntity getClassTransactionDataWithType(Long classId, int page, String type){


        //존재하는학급인지
        responseService.isExistClass(classId);



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
            StudentJobDto managerDto = (StudentJobDto) getStudentJobDto(t.getManagerId(), t.getManagerJobId()).getResult();
            StudentJobDto studentDto = (StudentJobDto) getStudentJobDto(t.getStudentId(), t.getStudentJobId()).getResult();

            TransactionCategory category = transactionCategoryRepository.findById(t.getCategoryId())
                    .orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CATEGORY));


            ClassTransactionDataDto transaction = ClassTransactionDataDto.builder()
                    .transactionId(t.getId())
                    .transactionDate(t.getCreateTimestamp().toLocalDateTime().toLocalDate())
                    .category(category.getTitle())
                    .detail(t.getDetail())
                    .isDeposit(isDeposit)
                    .transactionMoney(t.getMoney())
                    .manager(managerDto)
                    .student(studentDto)
                    .build();

            classTransactionDatas.add(transaction);

        }

        return responseService.successHandler(
                ClassTransactionResponseDto.builder()
                .currentPage(page)
                .maxPage(maxPage)
                .totalData(totalData)
                .classTransactionData(classTransactionDatas)
                .build()
            );
    }


    //student Id를 주면 stduentjobDto를 반환해주는 함수
    public BanklassResponseEntity getStudentJobDto(Long studentId, Long studentJobId){


        Student s = studentRepository.findById(studentId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST));

        UserInfo u = userInfoRepository.findById(s.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST));

        return responseService.successHandler(StudentJobDto.builder()
                .studentId(studentId)
                .number(s.getNumber())
                .studentName(u.getUserName())
                .jobId(studentJobId) //주의 : student를 찾아서 걔의 jobId를 가져오면 직업이 변경되면 데이터 로그도 변경됨!! 로그는 그 당시 직업을 저장
                .jobTitle(jobRepository.findById(studentJobId).get().getTitle())
                .build());
    }



    public BanklassResponseEntity getCategoryList(String type){



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

        return responseService.successHandler(
                result
        );
    }




    public BanklassResponseEntity transferMoney(TransferMoneyRequestDto transferMoneyRequestDto){

                /*
        1. 학생의 계좌의 잔고를 확인함
            -> 부족하면 에러 발생시켜야함
        2. 학생 계좌에서 돈을 출금함(minus)
        3. 국고 계좌에 돈을 추가함(plus)
        4. transfer_data table에 데이터를 추가함
         */


        Student manager = studentRepository.findById(transferMoneyRequestDto.getManagerId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));
        Student student = studentRepository.findById(transferMoneyRequestDto.getStudentId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));


        //0. "매니저"가 송금 권한이 있는 (직업의) 학생인가
        responseService.unavailableJobTransfer(manager.getJobId());


        //1. 학생의 계좌의 잔고를 확인한다.
        //송금하려는 금액이 계좌에 충분히 있는지 검사
        responseService.unavailableTransfer(transferMoneyRequestDto.getStudentId(), transferMoneyRequestDto.getMoney());

        //2. 학생 계좌 잔고를 수정한다
        int studentMoney = updateTransferStudentMoney(student.getId(), transferMoneyRequestDto.getMoney());


        //3.국고 계좌에 돈을 추가함(plus)
        int classMoney = updateTransferClassMoney(student.getClassId(), transferMoneyRequestDto.getMoney());

        //4.transfer_data table에 데이터 추가

        try {

            TransactionData transactionData = transactionDataRepository.save(TransactionData.builder()
                    .money(transferMoneyRequestDto.getMoney())
                    .studentMoney(studentMoney)
                    .classMoney(classMoney)
                    .managerId(manager.getId())
                    .managerJobId(manager.getJobId())
                    .studentId(student.getId())
                    .studentJobId(student.getJobId())
                    .classId(student.getClassId())
                    .categoryId(transferMoneyRequestDto.getCategoryId())
                    .detail(transferMoneyRequestDto.getDetail())
                    .from(STUDENT.getTypeName()) //이체하기 (학생 잔고에서 뽑아오는 것) FROM 학생
                    .build());


            return responseService.successHandler(
                    CreatedUriDto.builder()
                        .status("created")
                        .url( responseService.createUri(transactionData.getId(), UriTypes.TRANSACTION))
                        .build()
            );

        }catch(Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_TRANSFER);
        }

    }



    public BanklassResponseEntity payMoney(TransferMoneyRequestDto transferMoneyRequestDto){

        /*
        1. 국고의 잔고를 확인함
            -> 부족하면 에러 발생시켜야함
        2. 국고 계좌에서 돈을 출금함(minus)
        3. 학생 계좌에 돈을 추가함(plus)
        4. transfer_data table에 데이터를 추가함
         */

        Student manager = studentRepository.findById(transferMoneyRequestDto.getManagerId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));
        Student student = studentRepository.findById(transferMoneyRequestDto.getStudentId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));


        //0. "매니저"가 권한이 있는 (직업의) 학생인가
        responseService.unavailableJobPay(manager.getJobId());


        //1. 국고의 잔고를 확인한다.
        //송금하려는 금액이 계좌에 충분히 있는지 검사
        responseService.unavailablePay(student.getClassId(), transferMoneyRequestDto.getMoney());

        //2. 국고 잔고를 수정한다
        int classMoney = updatePayClassMoney(student.getClassId(), transferMoneyRequestDto.getMoney());



        //3.학생 계좌에 돈을 추가함(plus)
        int studentMoney = updatePayStudentMoney(student.getId(), transferMoneyRequestDto.getMoney());

        //4.transfer_data table에 데이터 추가


        try {

            TransactionData transactionData = transactionDataRepository.save(TransactionData.builder()
                    .money(transferMoneyRequestDto.getMoney())
                    .studentMoney(studentMoney)
                    .classMoney(classMoney)
                    .managerId(manager.getId())
                    .managerJobId(manager.getJobId()) //현재 가지고 있는 직업이 저장
                    .studentId(student.getId())
                    .studentJobId(student.getJobId())
                    .classId(student.getClassId())
                    .categoryId(transferMoneyRequestDto.getCategoryId())
                    .detail(transferMoneyRequestDto.getDetail())
                    .from(CLASS.getTypeName()) //지급하기 (국고 잔고에서 뽑아오는 것) FROM class
                    .build());


            return responseService.successHandler(
                    CreatedUriDto.builder()
                            .status("created")
                            .url(responseService.createUri(transactionData.getId(), UriTypes.TRANSACTION))
                            .build()
            );

        }catch (Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_PAY);
        }

    }




    public int updateTransferStudentMoney(Long studentId, int transferMoney){


        Student student = studentRepository.findById(studentId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));

        //객체의 돈을 변경하여 새로운 객체를 생성
        Student updateStudent = student.updateMoney(student.getMoney() - transferMoney); //보유금액 - 출금금액

        try {

            studentRepository.save(updateStudent);

            return updateStudent.getMoney();
        }catch(Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_TRANSFER);
        }

    }

    public int updateTransferClassMoney(Long classId, int transferMoney){

        //속해있는 학급 구하기
        Classes studentClass = classRepository.findById(classId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));

        //객체의 돈을 변경하여 새로운 객체를 생성
        Classes updateClass = studentClass.updateMoney(studentClass.getMoney() + transferMoney); //보유금액 + 출금금액

        try {
            classRepository.save(updateClass);

            return updateClass.getMoney();

        }catch(Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_TRANSFER);
        }
    }


    public int updatePayClassMoney(Long classId, int transferMoney){


        //속해있는 학급 구하기
        Classes studentClass = classRepository.findById(classId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));

        //객체의 돈을 변경하여 새로운 객체를 생성
        Classes updateClass = studentClass.updateMoney(studentClass.getMoney() - transferMoney); //보유금액 - 출금금액 (돈사용)

        try {
            classRepository.save(updateClass);

            return updateClass.getMoney();

        }catch (Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_PAY);
        }
    }

    public int updatePayStudentMoney(Long studentId, int transferMoney){


        Student student = studentRepository.findById(studentId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));

        //객체의 돈을 변경하여 새로운 객체를 생성
        Student updateStudent = student.updateMoney(student.getMoney() + transferMoney); //보유금액 + 출금금액

        try {
            studentRepository.save(updateStudent);

            return updateStudent.getMoney();

        }catch (Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE_PAY);
        }
    }

    //학생계좌에서 상세보기를 조회한 경우
    public BanklassResponseEntity getStudentTransactionDetail(Long transactionId){


        TransactionData t = transactionDataRepository.findById(transactionId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_TRANSACTION_DATA));


        // class에서 온 돈인경우 true
        boolean isDeposit = t.getFrom().equals("class");

        //학급 화폐
        Classes c = classRepository.findById(t.getClassId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));


        //거래 카테고리
        TransactionCategory tc = transactionCategoryRepository.findById(t.getCategoryId()).get();


        //매니저 정보 찾아내기
        Student m = studentRepository.findById(t.getManagerId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));
        UserInfo mu = userInfoRepository.findById(m.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));
        Job mj = jobRepository.findById(t.getManagerJobId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_JOB));

        StudentInfoDto managerInfo = StudentInfoDto.builder()
                .studentId(m.getId())
                .studentName(mu.getUserName())
                .studentJob(mj.getTitle())
                .studentNumber(m.getNumber())
                .build();


        //거래 본인
        Student s = studentRepository.findById(t.getStudentId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));
        UserInfo su = userInfoRepository.findById(s.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));
        Job sj = jobRepository.findById(t.getStudentJobId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_JOB));



        String depositAccount = "";
        String withdrawAccount = "";

        if(isDeposit){ //입금인 경우
            depositAccount = sj.getTitle() +" " +su.getUserName(); //입금이 학생정보
            withdrawAccount = c.getTitle();//출금은 학급정보
        }else{//출금인 경우
            withdrawAccount = sj.getTitle() +" " +su.getUserName(); //출금계좌가 학생계좌
            depositAccount = c.getTitle();//입금은 학급정보
        }



        return responseService.successHandler(
                TransactionDetailResponseDto.builder()
                .transactionId(t.getId())
                .transactionMoney(t.getMoney())
                .plus(isDeposit)
                .currency(c.getCurrency())
                .category(tc.getTitle())
                .depositAccount(depositAccount)
                .withdrawAccount(withdrawAccount)
                .managerInfo(managerInfo)
                .detail(t.getDetail())
                .transactionDate(t.getCreateTimestamp().toLocalDateTime())
                .leftMoney(t.getStudentMoney()) //학생기준 거래잔금임
                .build()
        );


    }



    //국고에서 상세보기를 조회한 경우
    public BanklassResponseEntity getClassTransactionDetail(Long transactionId){


        TransactionData t = transactionDataRepository.findById(transactionId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_TRANSACTION_DATA));


        // student에서 온 돈인경우 true(입금)
        boolean isDeposit = t.getFrom().equals("student");

        //학급 화폐
        Classes c = classRepository.findById(t.getClassId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));


        //거래 카테고리
        TransactionCategory tc = transactionCategoryRepository.findById(t.getCategoryId()).get();


        //매니저 정보 찾아내기
        Student m = studentRepository.findById(t.getManagerId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));
        UserInfo mu = userInfoRepository.findById(m.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));
        Job mj = jobRepository.findById(t.getManagerJobId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_JOB));

        StudentInfoDto managerInfo = StudentInfoDto.builder()
                .studentId(m.getId())
                .studentName(mu.getUserName())
                .studentJob(mj.getTitle())
                .studentNumber(m.getNumber())
                .build();


        //거래 본인
        Student s = studentRepository.findById(t.getStudentId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));
        UserInfo su = userInfoRepository.findById(s.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));
        Job sj = jobRepository.findById(t.getStudentJobId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_JOB));



        String depositAccount = "";
        String withdrawAccount = "";

        if(isDeposit){ //학급 기준으로 입금인 경우
            withdrawAccount = sj.getTitle() +" " +su.getUserName(); //출금 계좌가 학생계좌
            depositAccount = c.getTitle();//입금계좌가 학급이름
        }else{//출금인 경우
            depositAccount = sj.getTitle() +" " +su.getUserName(); //입금계좌가 학생계좌
            withdrawAccount = c.getTitle();//출금계좌가 학급정보
        }



        return responseService.successHandler(
                TransactionDetailResponseDto.builder()
                .transactionId(t.getId())
                .transactionMoney(t.getMoney())
                .plus(isDeposit)
                .currency(c.getCurrency())
                .category(tc.getTitle())
                .depositAccount(depositAccount)
                .withdrawAccount(withdrawAccount)
                .managerInfo(managerInfo)
                .detail(t.getDetail())
                .transactionDate(t.getCreateTimestamp().toLocalDateTime())
                .leftMoney(t.getClassMoney()) //학급기준 거래잔금임
                .build()
        );


    }




    public BanklassResponseEntity getStudentChangedMoney(Long studentId){

        //학생
        Student s = studentRepository.findById(studentId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));


        int currentMoney = s.getMoney();





        /*
        학생 거래 조회
        오늘을 제외한 거래 중에 가장 최근 거래(1개) 를 조회
        거래가 존재하지 않는다면 ERROR 말고 프론트에서 요청한 값으로 커스텀해서 보내기

         */
        TransactionData t = transactionDataRepository.findLastTransactionStudent(studentId)
                .orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_TRANSACTION_DATA));

        ///
//        if(t.getMoney() == -1){
//            return MoneyChangeResponseDto.builder()
//                    .isPlus(true)
//                    .changeMoney(0)
//                    .lastDay(-1)
//                    .build();
//        }


        int lastMoney = t.getStudentMoney();

        //마지막 거래일
        LocalDate lastdate = t.getCreateTimestamp().toLocalDateTime().toLocalDate();
        LocalDate today = LocalDate.now();

        //LocalDate차이를 일자로 계산하기
        long days = ChronoUnit.DAYS.between(lastdate,today);

        if(lastMoney<=currentMoney){

            return responseService.successHandler(
                    MoneyChangeResponseDto.builder()
                    .isPlus(true)
                    .changeMoney(currentMoney-lastMoney)
                    .lastDay(days)
                    .build()
                );

        }else{

            return responseService.successHandler(
                    MoneyChangeResponseDto.builder()
                    .isPlus(false)
                    .changeMoney(lastMoney-currentMoney)
                    .lastDay(days)
                    .build()
                );

        }


    }



    public BanklassResponseEntity getClassChangedMoney(Long classId){

        //학급 구하기
        Classes c = classRepository.findById(classId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));
        //현 국고 잔고
        int currentMoney = c.getMoney();





        /*
        학급 거래 조회
        오늘을 제외한 거래 중에 가장 최근 거래(1개) 를 조회
        거래가 존재하지 않는다면 ERROR 말고 프론트에서 요청한 값으로 커스텀해서 보내기

         */
        TransactionData t = transactionDataRepository.findLastTransactionClass(classId)
                .orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_TRANSACTION_DATA));

        ///
//        if(t.getMoney() == -1){
//            return MoneyChangeResponseDto.builder()
//                    .isPlus(true)
//                    .changeMoney(0)
//                    .lastDay(-1)
//                    .build();
//        }


        int lastMoney = t.getClassMoney();

        //마지막 거래일
        LocalDate lastdate = t.getCreateTimestamp().toLocalDateTime().toLocalDate();
        LocalDate today = LocalDate.now();

        //LocalDate차이를 일자로 계산하기
        long days = ChronoUnit.DAYS.between(lastdate,today);

        if(lastMoney<=currentMoney){

            return responseService.successHandler(MoneyChangeResponseDto.builder()
                    .isPlus(true)
                    .changeMoney(currentMoney-lastMoney)
                    .lastDay(days)
                    .build()
            );

        }else{

            return responseService.successHandler(
                    MoneyChangeResponseDto.builder()
                    .isPlus(false)
                    .changeMoney(lastMoney-currentMoney)
                    .lastDay(days)
                    .build()
                );
        }


    }




}
