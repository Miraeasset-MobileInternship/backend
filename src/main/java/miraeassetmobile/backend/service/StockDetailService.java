package miraeassetmobile.backend.service;

import miraeassetmobile.backend.controller.StockDetailController;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNews;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.*;
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

import static miraeassetmobile.backend.domain.enums.RecommendedTrendTypes.*;

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


    public BanklassResponseEntity getRecommendationTrend(String symbol, String period){

        /*
          period : 0m / -1m / -2m / -3m
         */

        List<Trend> trends = yhFinanceApiService.getRecommendationTrend(symbol);

        List<RecommendTrendGraphData> recommendTrendGraphData = new ArrayList<>();

        for (Trend t :trends) {

            if(t.getPeriod().equals(period)){

                recommendTrendGraphData.add(
                        RecommendTrendGraphData.builder()
                                .id(STRONGBUY.getTypeName())
                                .label(STRONGBUY.getTypeName())
                                .value(t.getStrongBuy())
                                .color(STRONGBUY.getColorCode())
                                .build()
                );

                recommendTrendGraphData.add(
                        RecommendTrendGraphData.builder()
                                .id(BUY.getTypeName())
                                .label(BUY.getTypeName())
                                .value(t.getBuy())
                                .color(BUY.getColorCode())
                                .build()
                );

                recommendTrendGraphData.add(
                        RecommendTrendGraphData.builder()
                                .id(HOLD.getTypeName())
                                .label(HOLD.getTypeName())
                                .value(t.getHold())
                                .color(HOLD.getColorCode())
                                .build()
                );

                recommendTrendGraphData.add(
                        RecommendTrendGraphData.builder()
                                .id(SELL.getTypeName())
                                .label(SELL.getTypeName())
                                .value(t.getSell())
                                .color(SELL.getColorCode())
                                .build()
                );

                recommendTrendGraphData.add(
                        RecommendTrendGraphData.builder()
                                .id(STRONGSELL.getTypeName())
                                .label(STRONGSELL.getTypeName())
                                .value(t.getStrongSell())
                                .color(STRONGSELL.getColorCode())
                                .build()
                );

                break;

            }

        }
        


        
        return responseService.successHandler(
                recommendTrendGraphData
        );

    }


    public long calculateTime(String pubDate) throws ParseException {


        //1. Date로 type변경
        //"Tue, 28 Mar 2023 01:31:00 +0000"
        //변경하는 법
        //https://stackoverflow.com/questions/32911677/what-is-date-format-of-eee-dd-mmm-yyyy-hhmmssz
        //https://www.tabnine.com/code/java/methods/java.text.DateFormat/parse
        // 패턴 리스트
        //http://www.java2s.com/ref/java/java-datetimeformatter-patterns.html
        // +0000 관련(서머타임 없는 걸로 아는데 1시간이 적용이 안되어서 문제되면 고쳐야함)
        // https://stackoverflow.com/questions/12305826/what-does-0000-mean-in-the-context-of-a-date-returned-by-the-twitter-api
        SimpleDateFormat curFormater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ssZ",Locale.UK);
        Date pubDateTime = curFormater.parse(pubDate);

        //2. 현 시각을 구함
        Date today = new Date();

        //3. 현시각 - (기사가 올라간 시각) -> 초단위
        //https://coding-factory.tistory.com/737 : Date타입 연산법
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





    public BanklassResponseEntity getChartData(String interval, String range, String symbol){

        FinanceSpark financeSpark = yhFinanceApiService.getFinanceSpark(interval,range, symbol);

        List<Long> timestamp = financeSpark.getTimestamp();
        List<Double> close = financeSpark.getClose();


        List<PriceData> priceData = new ArrayList<>();

        int minPrice = 999999;
        int maxPrice = 0;
        for(int i=0; i<timestamp.size(); i++){

            int v = (int) Math.ceil(close.get(i));

            if(v>maxPrice) maxPrice=v;
            if(v<minPrice) minPrice=v;

            priceData.add(
                    PriceData.builder()
                            .time(timestamp.get(i))
                            .price(close.get(i))
                            .build()
            );

        }


        DateInfo dateInfo;

        if(range.equals("1d")){

            dateInfo = DateInfo.builder()
                    .minDate(timestamp.get(0))
                    .maxDate(timestamp.get(0)+23400) // 장이 열린 시간 + (6시간 30분:장이 열리는 시간) 을 초로 환산
                    .build();

        }else{

            dateInfo = DateInfo.builder()
                    .maxDate(timestamp.get(timestamp.size()-1)) //마지막 값
                    .minDate(timestamp.get(0))
                    .build();

        }



        return responseService.successHandler(

                StockPriceGraphDataResponseDto.builder()
                        .symbol(symbol)
                        .period(range)
                        .dateInfo(dateInfo)
                        .priceInfo(
                                PriceInfo.builder()
                                        .minPrice(minPrice)
                                        .maxPrice(maxPrice)
                                        .build()

                        )
                        .data(priceData)
                        .build()

        );


    }


}
