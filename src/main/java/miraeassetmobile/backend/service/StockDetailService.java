package miraeassetmobile.backend.service;

import miraeassetmobile.backend.controller.StockDetailController;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.SimilarSymbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.dto.stockdetails.SimilarStockInfo;
import miraeassetmobile.backend.domain.dto.stockdetails.SimilarStockResponseDto;
import miraeassetmobile.backend.domain.dto.stockdetails.StockDetailResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.TagInfo;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockDetailService {

    YhFinanceApiService yhFinanceApiService;
    YhFinanceRapidApiService yhFinanceRapidApiService;
    ResponseService responseService;


    StockDetailService(YhFinanceApiService yhFinanceApiService, YhFinanceRapidApiService yhFinanceRapidApiService, ResponseService responseService){
        this.responseService = responseService;
        this.yhFinanceApiService = yhFinanceApiService;
        this.yhFinanceRapidApiService = yhFinanceRapidApiService;
    }


    public BanklassResponseEntity getDetailStockInfo(String symbol){

        FinanceQuote f = yhFinanceApiService.getFinanceQuote(symbol);


        double price = Math.round(f.getRegularMarketPrice()*100)/100.0;
        double changePrice = Math.round(f.getRegularMarketChange()*100)/100.0;
        double changePercent = Math.round(f.getRegularMarketChangePercent()*10)/10.0;


        return responseService.successHandler(
                StockDetailResponseDto.builder()
                        .symbol(f.getSymbol())
                        .stockTitle(f.getShortName())
                        .price(price)
                        .changePrice(changePrice)
                        .changePercent(changePercent)
                        .tagInfo(
                                TagInfo.builder()
                                        .type(f.getTypeDisp())
                                        .market(f.getFullExchangeName())
                                        .customPriceConfidence(f.getCustomPriceAlertConfidence())
                                        .isOpen(f.getMarketState().equals("REGULAR"))
                                        .build()
                        )
                        .build()
        );


    }



    public BanklassResponseEntity getSimilarStocks(String symbol){

        List<SimilarSymbol> similars = yhFinanceApiService.getSimilarSymbol(symbol);

        String symbols = "";
        for (SimilarSymbol ss :similars) {
            symbols += ss.getSymbol() + ",";
        }

        symbols += symbol; //기준이 되는 stock정보도 함께 요청

        List<FinanceQuote> fq = yhFinanceApiService.getFinanceQuotes(symbols);


        List<SimilarStockInfo> resultList = new ArrayList<>();

        for(int i=0; i<fq.size()-1; i++) {

            FinanceQuote f = fq.get(i);

            double price = Math.round(f.getRegularMarketPrice()*100)/100.0;
            double changePrice = Math.round(f.getRegularMarketChange()*100)/100.0;
            double changePercent = Math.round(f.getRegularMarketChangePercent()*10)/10.0;

            resultList.add(
            SimilarStockInfo.builder()
                    .symbol(f.getSymbol())
                    .stockTitle(f.getShortName())
                    .price(price)
                    .changePrice(changePrice)
                    .changePercent(changePercent)
                    .build()
            );
        }

        //맨 마지막 항목은 기존것
        FinanceQuote basic = fq.get(fq.size()-1);

        return responseService.successHandler(

                SimilarStockResponseDto.builder()
                        .totalData(resultList.size())
                        .stockTitle(basic.getShortName())
                        .stockInfoList(resultList)
                        .build()

        );




    }


}
