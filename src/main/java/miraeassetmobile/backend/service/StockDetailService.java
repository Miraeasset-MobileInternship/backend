package miraeassetmobile.backend.service;

import miraeassetmobile.backend.controller.StockDetailController;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNews;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.SimilarSymbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.dto.stockdetails.*;
import miraeassetmobile.backend.domain.dto.stocks.MarketNewsDto;
import miraeassetmobile.backend.domain.dto.stocks.MarketNewsResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.TagInfo;
import miraeassetmobile.backend.service.api.NaverTranslatorApiService;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import miraeassetmobile.backend.service.comparator.ListComparator;
import miraeassetmobile.backend.service.comparator.StockNewsListComparator;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StockDetailService {

    YhFinanceApiService yhFinanceApiService;
    YhFinanceRapidApiService yhFinanceRapidApiService;
    NaverTranslatorApiService naverTranslatorApiService;
    ResponseService responseService;


    StockDetailService(NaverTranslatorApiService naverTranslatorApiService, YhFinanceApiService yhFinanceApiService, YhFinanceRapidApiService yhFinanceRapidApiService, ResponseService responseService){
        this.responseService = responseService;
        this.yhFinanceApiService = yhFinanceApiService;
        this.yhFinanceRapidApiService = yhFinanceRapidApiService;
        this.naverTranslatorApiService = naverTranslatorApiService;
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




    public BanklassResponseEntity getStockNews(String symbol, String lang) throws ParseException {


        List<StockNews> newsList = yhFinanceRapidApiService.getStockMarketNews(symbol);

        Collections.sort(newsList, new StockNewsListComparator()); //결과값 최신순으로 정렬

        List<StockNewsDto> result = new ArrayList<>();

        if(lang.equals("ko")) {

            for (StockNews n : newsList) {

                String date = changeTime(calculateTime(n.getPubDate()),"ko");

                result.add(

                        StockNewsDto.builder()
                                .title(naverTranslatorApiService.translateToKo(n.getTitle()))
                                .link(n.getLink())
                                .date(date)
                                .build()

                );


            }

        }else{ //영어

            for (StockNews n : newsList) {

                String date = changeTime(calculateTime(n.getPubDate()),"en");

                result.add(

                        StockNewsDto.builder()
                                .title(n.getTitle())
                                .link(n.getLink())
                                .date(date)
                                .build()

                );


            }


        }

        return responseService.successHandler(
                StockNewsResponseDto.builder()
                        .totalData(result.size())
                        .stockNewsList(result)
                        .build()
        );



    }



    public long calculateTime(String pubDate) throws ParseException {


        //1. Date로 type변경
        //"Tue, 28 Mar 2023 01:31:00 +0000"
        //https://stackoverflow.com/questions/32911677/what-is-date-format-of-eee-dd-mmm-yyyy-hhmmssz
        SimpleDateFormat curFormater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ssZ",Locale.UK);
        Date pubDateTime = curFormater.parse(pubDate);

        //2. 현 시각을 구함
        Date today = new Date();

        //3. 현시각 - (기사가 올라간 시각) -> 초단위
        long sec = (today.getTime() - pubDateTime.getTime())/1000;


        return sec;
    }



    // 초를 ㅇㅇ분 전/ ㅇㅇ시간 전 / ㅇㅇ 일 전 : 형태로 변경
    public String changeTime(long second, String lang){

        String mins = lang.equals("ko")? "분 전" : "minutes ago";
        String hours = lang.equals("ko")? "시간 전" : "hours ago";
        String days = lang.equals("ko")? "일 전" : "days ago";



        //분
        long min = second/60;

        if(min>=60){ // 1시간을 넘어가는 범위 -> 시간 단위로 보여줘야함

            long hour = second/3600;


            if(hour>=24){ //하루를 넘어가는 범위 -> 일 단위로 보여야함

                long day = second/(24*60*60);

                return day + days;

            }

            return hour + hours;

        }

        return min+ mins;




    }


//    public BanklassResponseEntity getRecommendationTrend(String symbol){
//
//
//
//
//    }


}
