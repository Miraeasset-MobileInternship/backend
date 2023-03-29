package miraeassetmobile.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNews;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceSpark;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.SimilarSymbol;
import miraeassetmobile.backend.domain.dto.stockdetails.SimilarStockResponseDto;
import miraeassetmobile.backend.domain.dto.stockdetails.StockDetailResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.MarketNewsResponseDto;
import miraeassetmobile.backend.service.StockDetailService;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RequestMapping("/api/stock-detail")
@RestController
public class StockDetailController {

    YhFinanceApiService yhFinanceApiService;
    YhFinanceRapidApiService yhFinanceRapidApiService;
    StockDetailService stockDetailService;


    StockDetailController(StockDetailService stockDetailService, YhFinanceApiService yhFinanceApiService, YhFinanceRapidApiService yhFinanceRapidApiService){
        this.yhFinanceApiService = yhFinanceApiService;
        this.yhFinanceRapidApiService = yhFinanceRapidApiService;
        this.stockDetailService = stockDetailService;
    }


    @GetMapping("/spark-test")
    public ResponseEntity<FinanceSpark> getFinanceSparkTest(String interval, String range, String symbol){
        return ResponseEntity.ok(yhFinanceApiService.getFinanceSpark(interval,range,symbol));
    }


    @GetMapping("/stock-news")
    public ResponseEntity<List<StockNews>> getStockMarketNews(String symbol){
        return ResponseEntity.ok(yhFinanceRapidApiService.getStockMarketNews(symbol));
    }

//    @GetMapping("stock-similar")
//    public ResponseEntity<List<SimilarSymbol>> getSimilarStocks(String symbol){
//        return ResponseEntity.ok(yhFinanceApiService.getSimilarSymbol(symbol));
//    }


//    @GetMapping("summary")
//    public ResponseEntity getSummary(String symbol, String modules){
//        return ResponseEntity.ok(yhFinanceApiService.getSymbolDetail(symbol,modules));
//    }


    @GetMapping("/summary")
    public ResponseEntity getSummary(String symbol){
        return ResponseEntity.ok(yhFinanceApiService.getRecommendationTrend(symbol));
    }

    //주식 상세 정보
    @GetMapping("/{stock_id}")
    @Operation(summary = "상세 주식 정보 보기 기능", description = "주식 종목 상세 정보",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = StockDetailResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getDetailInfo(@PathVariable(value = "stock_id") String symbol){
        return ResponseEntity.ok(stockDetailService.getDetailStockInfo(symbol));
    }


    //유사 종목 추천
    @GetMapping("/{stock_id}/similar")
    @Operation(summary = "유사 종목 리스트", description = "유사한 주식 종목 리스트",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = SimilarStockResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getSimilarStocks(@PathVariable(value = "stock_id") String symbol){
        return ResponseEntity.ok(stockDetailService.getSimilarStocks(symbol));
    }



    //종목관련 뉴스
    @GetMapping("/{stock_id}/news")
    @Operation(summary = "종목 관련 뉴스", description = "종목 관련 뉴스",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = MarketNewsResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getStockNews(@PathVariable(value = "stock_id") String symbol) throws ParseException {
        return ResponseEntity.ok(stockDetailService.getStockNews(symbol,"en"));
    }


    //투자 동향
    @GetMapping("/{stock_id}/recommend-trend")
    @Operation(summary = "최근 투자 동향", description = "최근 투자 동향 그래프",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = MarketNewsResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getRecommendationTrend(@PathVariable(value = "stock_id") String symbol
                                                                        , @RequestParam(defaultValue = "0m") String period){
        return ResponseEntity.ok(stockDetailService.getRecommendationTrend(symbol,period));
    }


    //
    @GetMapping("/{stock_id}/chart")
    @Operation(summary = "차트 그래프 데이터 얻기", description = "차트 그래프 데이터 얻기",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = MarketNewsResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getChartData(@RequestParam(defaultValue = "15m") String interval,
                                                            @RequestParam(defaultValue = "1d") String range,
                                                            @PathVariable(value = "stock_id") String symbol){
        return ResponseEntity.ok(stockDetailService.getChartData(interval,range,symbol));
    }

}
