package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.BanklassResponseEntity;

import miraeassetmobile.backend.domain.dto.stocks.*;
import miraeassetmobile.backend.service.StockService;

import miraeassetmobile.backend.service.api.NaverTranslatorApiService;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
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
//    @Operation(description = "내 주식관련 총 집합 정보, 학생 - 내 주식페이지 상단 카드부분(평가금액, 매수금액 등)")
    @Operation(summary = "내 주식 전체 상태 정보", description = "내 주식관련 총 집합 정보, 학생 - 내 주식페이지 상단 카드부분(평가금액, 매수금액 등)",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = TotalStockInfoResponseDto.class))),
                    @ApiResponse(responseCode = "E901", description = "학생이 보유하지 않은 주식종목을 조회한 경우", content = @Content),
                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getStudentAccountInfo(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(stockService.getTotalStockStatus(studentId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }

    @GetMapping("/trending")
    @Operation(summary = "Todays trending", description = "trending 주식 리스트",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = TrendingStockListDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getTrendingStockList(){
        return ResponseEntity.ok(stockService.getTodayTrending());
    }


    @GetMapping("/{student_id}")
    @Operation(summary = "보유 주식종목 리스트", description = "보유한 주식종목 리스트",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = OwnStockInfoResponseDto.class))),
                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
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
//    @Operation(description = "자동완성 검색")
    @Operation(summary = "자동완성 검색 기능", description = "자동완성 검색 기능",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AutoComplete.class)))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getSearchAutoComplete(@RequestParam(value = "query") String query){
        return ResponseEntity.ok(stockService.getSearchAutoComplete(query)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }



    @GetMapping("/news/market")
//    @Operation(description = "경제 뉴스 전체")
    @Operation(summary = "경제 뉴스 전체", description = "경제 뉴스 전체",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MarketNewsResponseDto.class)))),
                    @ApiResponse(responseCode = "E504", description = "경제 뉴스 API 서버에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E502", description = "번역 과정에서 naver api에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E50X", description = "parseException 업데이트 예정", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getMarketNews(@RequestParam(defaultValue = "en") String lang) throws ParseException {
        return ResponseEntity.ok(stockService.getMarketNews(lang)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }



    @GetMapping("/check-selling")
    @Operation(summary = "매도 수량 및 가격 체크", description = "매도할 수 있는 주식의 수량 체크 및 현재가 체크",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = CheckForSellingStockResponseDto.class))),
                    @ApiResponse(responseCode = "E901", description = "학생이 보유하지 않은 주식종목을 조회한 경우", content = @Content),
                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getSellAmount(@RequestParam String stockId, @RequestParam Long studentId) {
        return ResponseEntity.ok(stockService.checkBeforeSelling(stockId,studentId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }


//    @PostMapping("/sell")
//    @Operation(summary = "매도 수량 및 가격 체크", description = "매도할 수 있는 주식의 수량 체크 및 현재가 체크",
//            responses = {
//                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = CheckForSellingStockResponseDto.class))),
//                    @ApiResponse(responseCode = "E901", description = "학생이 보유하지 않은 주식종목을 조회한 경우", content = @Content),
//                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
//                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content),
//                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
//            })
//    public ResponseEntity<BanklassResponseEntity> getSellAmount(@RequestParam String stockId, @RequestParam Long studentId) {
//        return ResponseEntity.ok(stockService.checkBeforeSelling(stockId,studentId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }



//    @GetMapping("/check-price")
//    @Operation(summary = "매수 가격 체크", description = "매도할 수 있는 주식의 수량 체크",
//            responses = {
//                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = SellingStockAmountResponseDto.class))),
//                    @ApiResponse(responseCode = "E901", description = "학생이 보유하지 않은 주식종목을 조회한 경우", content = @Content),
//                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
//            })
//    public ResponseEntity<BanklassResponseEntity> getPrice(@RequestParam String stockId) {
//        return ResponseEntity.ok(stockService.checkPriceByStockId(stockId)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }



//    @PostMapping("/sell-stock")
//    @Operation(summary = "매도 수량 체크", description = "매도할 수 있는 주식의 수량 체크",
//            responses = {
//                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = .class))),
//                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
//            })
//    public ResponseEntity<BanklassResponseEntity> BuyStock(@RequestBody @Valid StockBuyingRequestDto stockBuyingRequestDto) {
//        return ResponseEntity.ok(stockService.buyStock(stockBuyingRequestDto)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }
//
//
//    @PostMapping("/buy-stock")
//    @Operation(summary = "매도 수량 체크", description = "매도할 수 있는 주식의 수량 체크",
//            responses = {
//                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = .class))),
//                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
//            })
//    public ResponseEntity<BanklassResponseEntity> BuyStock(@RequestBody @Valid StockBuyingRequestDto stockBuyingRequestDto) {
//        return ResponseEntity.ok(stockService.buyStock(stockBuyingRequestDto)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
//    }

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
