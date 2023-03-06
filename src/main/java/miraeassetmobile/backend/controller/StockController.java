package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.api.StockApiResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.StudentStockInfoResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.TotalStockInfoResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentAccountResponseDto;
import miraeassetmobile.backend.service.StockApiCallService;
import miraeassetmobile.backend.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/stock")
@RestController
public class StockController {


    StockService stockService;


    StockController(StockService stockService){
        this.stockService=stockService;
    }


    @GetMapping("/total-info")
    @Operation(description = "내 주식관련 총 집합 정보, 학생 - 내 주식페이지 상단 카드부분(평가금액, 매수금액 등)")
    public ResponseEntity<TotalStockInfoResponseDto> getStudentAccountInfo(@RequestParam(value = "student_id") Long studentId){
        return ResponseEntity.ok(stockService.getTotalStockStatus(studentId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }



//    @GetMapping("/student/{student_id}")
//    @Operation(description = "보유 주식 종목별 정보, 학생 - 내 주식페이지 하단 보유 주식리스트 부분")
//    public ResponseEntity<List<StudentStockInfoResponseDto>> getStudentAccountInfo(@PathVariable(value = "student_id") Long studentId){
//        return ResponseEntity.ok(stockService.getStudentStockList(studentId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }




}
