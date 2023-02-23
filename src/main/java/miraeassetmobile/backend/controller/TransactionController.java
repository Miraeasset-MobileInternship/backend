package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.transactions.StudentTransactionResponseDto;
import miraeassetmobile.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/transaction")
@RestController
public class TransactionController {


    TransactionService transactionService;

    TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }



    @GetMapping("/student/{student_id}")
    @Operation(description = "해당 학생의 계좌 거래 내역을 조회(10개, 최신순), 학생 - 홈 화면 거래 내역")
    public ResponseEntity<List<StudentTransactionResponseDto>> getStudentTransactionData(@PathVariable(value = "student_id") Long studentId,
                                                                                         @RequestParam(defaultValue = "0") int page){

        return ResponseEntity.ok(transactionService.getStudentTransactionData(studentId,page));

    }



}
