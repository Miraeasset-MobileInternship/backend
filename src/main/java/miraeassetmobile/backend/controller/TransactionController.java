package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.transactions.StudentTransactionDataDto;
import miraeassetmobile.backend.domain.dto.transactions.StudentTransactionResponseDto;
import miraeassetmobile.backend.domain.dto.transactions.TransactionCategoryDto;
import miraeassetmobile.backend.domain.dto.transactions.TransferMoneyRequestDto;
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



//    @GetMapping("/student/{student_id}")
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


    @GetMapping("/category")
    @Operation(description = "출금/입금 카테고리 내역을 조회(SELECTOR), 학생 - 업무수행 - 특수업무 수행 중 출입금 카테고리SELECTOR")
    public ResponseEntity<List<TransactionCategoryDto>> getCategoryList(){
        return ResponseEntity.ok(transactionService.getCategoryList());
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

}
