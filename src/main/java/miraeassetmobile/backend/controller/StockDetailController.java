package miraeassetmobile.backend.controller;


import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceSpark;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/stock-detail")
@RestController
public class StockDetailController {

    YhFinanceApiService yhFinanceApiService;
    YhFinanceRapidApiService yhFinanceRapidApiService;


    StockDetailController(YhFinanceApiService yhFinanceApiService, YhFinanceRapidApiService yhFinanceRapidApiService){
        this.yhFinanceApiService = yhFinanceApiService;
        this.yhFinanceRapidApiService = yhFinanceRapidApiService;
    }


    @GetMapping("/spark-test")
    public ResponseEntity<FinanceSpark> getFinanceSparkTest(String interval, String range, String symbol){
        return ResponseEntity.ok(yhFinanceApiService.getFinanceSpark(interval,range,symbol));
    }


    @GetMapping("/stock-news")
    public ResponseEntity<List<MarketNews>> getStockMarketNews(String symbol){
        return ResponseEntity.ok(yhFinanceRapidApiService.getStockMarketNews(symbol));
    }


}
