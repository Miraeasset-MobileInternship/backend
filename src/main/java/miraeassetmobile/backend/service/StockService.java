package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.dto.stocks.TrendStockDto;
import miraeassetmobile.backend.domain.dto.stocks.TrendingStockListDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    YhFinanceApiService yhFinanceApiService;
    ResponseService responseService;

    StockService(YhFinanceApiService yhFinanceApiService,ResponseService responseService){
        this.yhFinanceApiService = yhFinanceApiService;
        this.responseService = responseService;
    }


    public BanklassResponseEntity getTodayTrending(){


        TrendingByRegion trending = yhFinanceApiService.getTrendingByRegion();

        List<Symbol> symbolList = trending.getQuotes();

        List<TrendStockDto> trendList = new ArrayList<>();


        for (Symbol s: symbolList) {

            String symbolCode = s.getSymbol();

            FinanceQuote f = yhFinanceApiService.getFinanceQuote(symbolCode);

            String price = String.format("%.2f",f.getRegularMarketPrice());
            String change = String.format("%.2f",f.getRegularMarketChange());
            String changePercent = String.format("%.2f",f.getRegularMarketChangePercent());

            boolean open = f.getMarketState().equals("REGULAR") ? true : false;

            trendList.add(TrendStockDto.builder()
                    .id(f.getSymbol())
                    .stockTitle(f.getShortName())
                    .price(price)
                    .change(change)
                    .changePercent(changePercent)
                    .type(f.getTypeDisp())
                    .market(f.getFullExchangeName())
                    .customPriceConfidence(f.getCustomPriceAlertConfidence())
                    .isOpen(open)
                    .build()
            );

        }

        return responseService.successHandler(

                TrendingStockListDto.builder()
                        .totalNum(trending.getCount())
                        .trendStockList(trendList)
                        .build()
        );



    }





}
