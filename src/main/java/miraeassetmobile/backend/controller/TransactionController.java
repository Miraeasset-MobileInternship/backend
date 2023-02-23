package miraeassetmobile.backend.controller;

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
    public ResponseEntity<List<StudentTransactionResponseDto>> getStudentTransactionData(@PathVariable(value = "student_id") Long studentId,
                                                                                         @RequestParam(defaultValue = "0") int page){

        return ResponseEntity.ok(transactionService.getStudentTransactionData(studentId,page));

    }



}
