package miraeassetmobile.backend.service;

import miraeassetmobile.backend.controller.StockDetailController;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.stockdetails.StockDetailResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.TagInfo;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import org.springframework.stereotype.Service;

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



        double changePrice = Math.round(f.getRegularMarketChange()*100)/100.0;
        double changePercent = Math.round(f.getRegularMarketChangePercent()*10)/10.0;


        return responseService.successHandler(
                StockDetailResponseDto.builder()
                        .symbol(f.getSymbol())
                        .stockTitle(f.getShortName())
                        .price(f.getRegularMarketPrice())
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


}
