package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;

import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.dto.stocks.TotalStockInfoResponseDto;
import miraeassetmobile.backend.service.StockService;
import miraeassetmobile.backend.service.YhFinanceApiService;
import miraeassetmobile.backend.service.YhFinanceRapidApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RequestMapping("/api/stock")
@RestController
public class StockController {



    StockService stockService;
    YhFinanceApiService yhFinanceApiService;
    YhFinanceRapidApiService yhFinanceRapidApiService;
    NaverTranslatorApiService naverTranslatorApiService;


    StockController(NaverTranslatorApiService naverTranslatorApiService, YhFinanceRapidApiService yhFinanceRapidApiService, StockService stockService, YhFinanceApiService yhFinanceApiService){
        this.stockService=stockService;
        this.yhFinanceApiService = yhFinanceApiService;
        this.yhFinanceRapidApiService = yhFinanceRapidApiService;
        this.naverTranslatorApiService=naverTranslatorApiService;
    }


    @GetMapping("/total-info/{student_id}")
    @Operation(description = "내 주식관련 총 집합 정보, 학생 - 내 주식페이지 상단 카드부분(평가금액, 매수금액 등)")
    public ResponseEntity<BanklassResponseEntity> getStudentAccountInfo(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(stockService.getTotalStockStatus(studentId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }

    @GetMapping("/trending")
    public ResponseEntity<BanklassResponseEntity> getFinanceQuote(){
        return ResponseEntity.ok(stockService.getTodayTrending());
    }


    @GetMapping("/{student_id}")
    public ResponseEntity<BanklassResponseEntity> getOwnedStockList(@PathVariable(value = "student_id") Long studentId,
                                                                    @RequestParam(defaultValue = "0") int page){
        return ResponseEntity.ok(stockService.getOwnedStockList(studentId,page));
    }



    //인기종목 Top N개
//    @GetMapping("/trending")
//    @Operation(description = "인기 종목 리스트")
//    public ResponseEntity getStudentAccountInfo(@RequestParam(value = "student_id") Long studentId){
//        return ResponseEntity.ok(stockService.getTotalStockStatus(studentId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }

    @GetMapping("/search-stocks")
    @Operation(description = "자동완성 검색")
    public ResponseEntity<BanklassResponseEntity> getSearchAutoComplete(@RequestParam(value = "query") String query){
        return ResponseEntity.ok(stockService.getSearchAutoComplete(query)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }


    @GetMapping("/news/market")
    @Operation(description = "경제 뉴스 전체")
    public ResponseEntity<BanklassResponseEntity> getMarketNews(@RequestParam(defaultValue = "ko") String lang) throws ParseException {
        return ResponseEntity.ok(stockService.getMarketNews(lang)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }

//    @GetMapping("/student/{student_id}")
//    @Operation(description = "보유 주식 종목별 정보, 학생 - 내 주식페이지 하단 보유 주식리스트 부분")
//    public ResponseEntity<List<StudentStockInfoResponseDto>> getStudentAccountInfo(@PathVariable(value = "student_id") Long studentId){
//        return ResponseEntity.ok(stockService.getStudentStockList(studentId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }



//    @GetMapping("/get/trending")
//    @Operation(description = "야후 파이낸스 테스트용")
//    public ResponseEntity<TrendingByRegion> trending(){
//
//        return yhFinanceApiService.getTrendingByRegion();
//        //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }



//    @GetMapping("/get/realtime")
//    @Operation(description = "야후 파이낸스 테스트용")
//    public void realtimeprice(@RequestParam String symbol) throws IOException {
//
//        yahooFinanceApiCallService.getRealtimePrice(symbol);
//        //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }
//
//
//    @GetMapping("/get/chart")
//    @Operation(description = "야후 파이낸스 테스트용")
//    public void realtimeprice(@RequestParam String period, @RequestParam String symbol) throws IOException {
//
//        yahooFinanceApiCallService.getChart(period,symbol);
//        //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }
//
//
//    @GetMapping("/get/autocomplete")
//    @Operation(description = "야후 파이낸스 테스트용")
//    public void realtimeprice(@RequestParam String region, @RequestParam String lang, @RequestParam String query) throws IOException {
//
//        yahooFinanceApiCallService.getAutocomplete(region, lang, query);
//        //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }


}
