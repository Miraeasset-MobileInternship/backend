package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.transactions.*;
import miraeassetmobile.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/transaction")
@RestController
public class TransactionController {


    TransactionService transactionService;

    TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }



//    @GetMapping("/student/{student_id}") -> 입출금 따로 조회 가능으로 대체되었지만 일단 임시로 살려둠(m-crew 버전에는 없음
//    @Operation(description = "해당 학생의 계좌 거래 내역을 조회(10개, 최신순), 학생 - 홈 화면 거래 내역")
//    public ResponseEntity<StudentTransactionResponseDto> getStudentTransactionData(@PathVariable(value = "student_id") Long studentId,
//                                                                                   @RequestParam(defaultValue = "0") int page){
//
//        return ResponseEntity.ok(transactionService.getStudentTransactionData(studentId,page));
//
//    }


    //
    @GetMapping("/student/{student_id}")
    @Operation(description = "해당 학생의 계좌 거래 내역을 조회(10개, 최신순, 입금(type=deposit),출금(type=withdraw) 구분), 학생 - 홈 화면 거래 내역")
    public ResponseEntity<StudentTransactionResponseDto> getStudentTransactionData(@PathVariable(value = "student_id") Long studentId,
                                                                                   @RequestParam(defaultValue = "all") String type,
                                                                                   @RequestParam(defaultValue = "0") int page){

        return ResponseEntity.ok(transactionService.getStudentTransactionDataWithType(studentId,page,type));

    }

    @GetMapping("/class/{class_id}")
    @Operation(description = "해당 학급(국고)의 계좌 거래 내역을 조회(10개, 최신순, 입금(type=deposit),출금(type=withdraw) 구분), 학생 - 국고 화면 거래 내역")
    public ResponseEntity<ClassTransactionResponseDto> getClassTransactionData(@PathVariable(value = "class_id") Long classId,
                                                                               @RequestParam(defaultValue = "all") String type,
                                                                               @RequestParam(defaultValue = "0") int page){

        return ResponseEntity.ok(transactionService.getClassTransactionDataWithType(classId,page,type));

    }


    @GetMapping("/category")
    @Operation(description = "출금/입금 카테고리 내역을 조회(SELECTOR), 학생 - 업무수행 - 특수업무 수행 중 출입금 카테고리SELECTOR")
    public ResponseEntity<TransactionCategoryListResonseDto> getCategoryList(@PathVariable String type){
        return ResponseEntity.ok(transactionService.getCategoryList(type));
    }




    //학생 계좌에서 돈을 뺀다.
    @PostMapping("/transfer") //이체
    @Operation(description = "이체기능 : 학생계좌에서 국고로 이체되는 기능, 학생 - 업무수행 - 특수업무 수행 중 학생 계좌 출금")
    public ResponseEntity transferMoneyFromStudent(@RequestBody @Valid TransferMoneyRequestDto transferMoneyRequestDto){

        /*
        파라미터
        누구의 계좌에서 돈을 출금 (student_id)
        출금 카테고리 (category_id)
        출금 사유(detail)
        출금 금액(money)
        담당자(manager_id) -> 이 행위를 수행한 사람의 pk
         */



        /*
        1. 학생의 계좌의 잔고를 확인함
            -> 부족하면 에러 발생시켜야함
        2. 학생 계좌에서 돈을 출금함(minus)
        3. 국고 계좌에 돈을 추가함(plus)
        4. transfer_data table에 데이터를 추가함
         */


        //새로 생성된 transaction data의 id URI를 반환함
        return ResponseEntity.created(transactionService.transferMoney(transferMoneyRequestDto)).build(); //id를 같이 반환하기

    }




    //국고에서 돈을 뺀다.
    @PostMapping("/pay") //지급
    @Operation(description = "지급기능 : 국고에서 학생계좌로 이체되는 기능, 학생 - 업무수행 - 특수업무 수행 중 국고 출금")
    public ResponseEntity payMoneyFromClass(@RequestBody @Valid TransferMoneyRequestDto transferMoneyRequestDto){

        /*
        파라미터
        누구의 계좌로 돈을 지급 (student_id)
        지급 카테고리 (category_id)
        지급 사유(detail)
        지급 금액(money)
        담당자(manager_id) -> 이 행위를 수행한 사람의 pk
         */



        /*
        1. 국고의 잔고를 확인함
            -> 부족하면 에러 발생시켜야함
        2. 국고 계좌에서 돈을 출금함(minus)
        3. 학생 계좌에 돈을 추가함(plus)
        4. transfer_data table에 데이터를 추가함
         */


        //새로 생성된 transaction data의 id URI를 반환함
        return ResponseEntity.created(transactionService.payMoney(transferMoneyRequestDto)).build(); //id를 같이 반환하기

    }

    @GetMapping("/student/detail/{transaction_id}")
    @Operation(description = "거래 상세보기 - 학생계좌 기준")
    public ResponseEntity<TransactionDetailResponseDto> getStudentTransactionDetail(@PathVariable(value = "transaction_id") Long transactionId){
        return ResponseEntity.ok(transactionService.getStudentTransactionDetail(transactionId));
    }

    @GetMapping("/class/detail/{transaction_id}")
    @Operation(description = "거래 상세보기 - 국고 조회 기준")
    public ResponseEntity<TransactionDetailResponseDto> getClassTransactionDetail(@PathVariable(value = "transaction_id") Long transactionId){
        return ResponseEntity.ok(transactionService.getClassTransactionDetail(transactionId));
    }

}
