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
import miraeassetmobile.backend.domain.dto.stockdetails.StockDetailResponseDto;
import miraeassetmobile.backend.service.StockDetailService;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/similar/{stock_id}")
    @Operation(summary = "유사 종목 리스트", description = "유사한 주식 종목 리스트",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = StockDetailResponseDto.class))),
                    @ApiResponse(responseCode = "E503", description = "주식 API 서버에서 발생한 에러", content = @Content),
            })
    public ResponseEntity<BanklassResponseEntity> getSimilarStocks(@PathVariable(value = "stock_id") String symbol){
        return ResponseEntity.ok(stockDetailService.getSimilarStocks(symbol));
    }


}
