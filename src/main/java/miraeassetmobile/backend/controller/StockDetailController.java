package miraeassetmobile.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNews;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceSpark;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.SimilarSymbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Trend;
import miraeassetmobile.backend.domain.dto.stockdetails.*;
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


    StockDetailService stockDetailService;
    YhFinanceApiService yhFinanceApiService;



    StockDetailController(YhFinanceApiService yhFinanceApiService, StockDetailService stockDetailService){
        this.stockDetailService = stockDetailService;
        this.yhFinanceApiService = yhFinanceApiService;
    }


    //for test
    @GetMapping("/one-quote")
    public ResponseEntity<FinanceQuote> getFinanceQ(String symbol){
        return ResponseEntity.ok(yhFinanceApiService.getFinanceQuote(symbol));
    }



    //주식 상세 정보
    @GetMapping("/{stock_id}")
    @Operation(summary = "상세 주식 정보 보기 기능", description = "주식 종목 상세 정보",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = StockDetailResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E905", description = "존재하지 않는 종목", content = @Content),
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
                    @ApiResponse(responseCode = "E420", description = "파라미터에 숫자가 아니고 다른게 온 경우 발생하는 에러", content = @Content),
                    @ApiResponse(responseCode = "E904", description = "해당 종목에서는 제공하지 않는 기능인 경우", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getStockNews(@PathVariable(value = "stock_id") String symbol,
                                                               @RequestParam(value = "num", defaultValue = "all") String num) throws ParseException {
        return ResponseEntity.ok(stockDetailService.getStockNews(symbol,"en", num));
    }



    //투자 동향
    @GetMapping("/{stock_id}/recommend-trend")
    @Operation(summary = "최근 투자 동향", description = "최근 투자 동향 그래프",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecommendTrendGraphData.class)))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E904", description = "해당 종목에서는 제공하지 않는 기능인 경우", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getRecommendationTrend(@PathVariable(value = "stock_id") String symbol
            , @RequestParam(defaultValue = "0m") String period){
        return ResponseEntity.ok(stockDetailService.getRecommendationTrend(symbol,period));
    }


    //차트데이터
    @GetMapping("/{stock_id}/chart")
    @Operation(summary = "차트 그래프 데이터 얻기", description = "차트 그래프 데이터 얻기",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = MarketNewsResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
                    @ApiResponse(responseCode = "E904", description = "해당 종목에서는 제공하지 않는 기능인 경우", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getChartData(@RequestParam(defaultValue = "1d") String range,
                                                               @PathVariable(value = "stock_id") String symbol){
        return ResponseEntity.ok(stockDetailService.getChartData(range,symbol));
    }


    //회사정보
    @GetMapping("/{stock_id}/company-info")
    @Operation(summary = "회사 정보", description = "상장 회사 정보",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = CompanyInfoResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getCompanyInfo(@PathVariable(value = "stock_id") String symbol){
        return ResponseEntity.ok(stockDetailService.getCompanyInfo(symbol));
    }


    @GetMapping("/{stock_id}/stock-info")
    @Operation(summary = "주식  종목 상세", description = "주식 종목 상세",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = StockInfoResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getStockInfo(@PathVariable(value = "stock_id") String symbol){
        return ResponseEntity.ok(stockDetailService.getStockInfo(symbol));
    }


    @GetMapping("/watch-list")
    @Operation(summary = "최다 조회 종목 ", description = "최다 조회 종목 순위",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = WatchedListResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getWatchList(@RequestParam(defaultValue = "5") int count,
                                                               @RequestParam(defaultValue = "day_gainers") String strIds) {
        return ResponseEntity.ok(stockDetailService.getWatchedList(count, strIds));
    }







}
