package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.BanklassResponseEntity;

import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.AutoComplete;
import miraeassetmobile.backend.domain.dto.stocks.*;
import miraeassetmobile.backend.service.StockService;

import miraeassetmobile.backend.service.api.NaverTranslatorApiService;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@RequestMapping("/api/stock")
@RestController
public class StockController {



    StockService stockService;
    YhFinanceApiService yhFinanceApiService;
    YhFinanceRapidApiService yhFinanceRapidApiService;
    NaverTranslatorApiService naverTranslatorApiService;


    StockController(NaverTranslatorApiService naverTranslatorApiService, YhFinanceRapidApiService yhFinanceRapidApiService, StockService stockService, YhFinanceApiService yhFinanceApiService){
        this.stockService = stockService;
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
                    @ApiResponse(responseCode = "E905", description = "존재하지 않는 종목이거나 서비스 할 수 없는 종목(가격이 0인경우)", content = @Content),
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
                    @ApiResponse(responseCode = "E905", description = "존재하지 않는 종목이거나 서비스 할 수 없는 종목(가격이 0인경우)", content = @Content),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getOwnedStockList(@PathVariable(value = "student_id") Long studentId,
                                                                    @RequestParam(defaultValue = "0") int page){
        return ResponseEntity.ok(stockService.getOwnedStockList(studentId,page));
    }



    @GetMapping("/search-stocks")
//    @Operation(description = "자동완성 검색")
    @Operation(summary = "자동완성 검색 기능", description = "자동완성 검색 기능",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = AutoCompleteResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getSearchAutoComplete(@RequestParam(value = "query") String query){
        return ResponseEntity.ok(stockService.getSearchAutoComplete(query)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }


    @GetMapping("/search-stocks-list")
    public ResponseEntity getSearchAutoCompleteList(){
        return ResponseEntity.ok(stockService.getSearchAutoCompleteList()); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }


    @GetMapping("/news/market")
//    @Operation(description = "경제 뉴스 전체")
    @Operation(summary = "경제 뉴스 전체", description = "경제 뉴스 전체",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = MarketNewsResponseDto.class))),
                    @ApiResponse(responseCode = "E504", description = "경제 뉴스 API 서버에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E502", description = "번역 과정에서 naver api에서 발생한 에러", content = @Content),
//                    @ApiResponse(responseCode = "E50X", description = "parseException 업데이트 예정", content = @Content),
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



    @PostMapping("/sell")
    @Operation(summary = "매도 기능", description = "매도할 수 있는 주식의 수량 체크 및 현재가 체크",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = CreatedUriDto.class))),
                    @ApiResponse(responseCode = "E901", description = "학생이 보유하지 않은 주식종목을 조회한 경우", content = @Content),
                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E902", description = "보유보다 더 많은 수량을 판매하려고 하는 경우", content = @Content),
                    @ApiResponse(responseCode = "E950", description = "데이터 저장 과정등에서 DB에서 발생한 에러 - 매도 통합 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getSell(@RequestBody @Valid StockSellingRequestDto stockSellingRequestDto) {
        return ResponseEntity.ok(stockService.sellshares(stockSellingRequestDto)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }

    @GetMapping("/check-buying")
    @Operation(summary = "매수 가격 체크", description = "매수 가격 및 필요정보 체크",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = CheckForBuyingStockResponseDto.class))),
                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> checkBeforeBuying(@RequestParam String stockId, @RequestParam Long studentId) {
        return ResponseEntity.ok(stockService.checkBeforeBuying(stockId, studentId));
    }


    @PostMapping("/buy")
    @Operation(summary = "매수 기능", description = "매수",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = CreatedUriDto.class))),
                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E903", description = "주문을 위해 필요한 돈이 충분하지 않음", content = @Content),
                    @ApiResponse(responseCode = "E950", description = "DB관련 저장/삭제/업데이트 에러 - 주식 거래 통합에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> buyStock(@RequestBody @Valid StockBuyingRequestDto stockBuyingRequestDto) {
        return ResponseEntity.ok(stockService.buyShares(stockBuyingRequestDto)); //api 에서는 1페이지 부턴데 우리는 0페이지부터로 합의함
    }


}
