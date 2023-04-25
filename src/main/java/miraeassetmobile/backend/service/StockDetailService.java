package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNews;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNewsTime;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.*;
import miraeassetmobile.backend.domain.dto.stockdetails.*;
import miraeassetmobile.backend.domain.dto.stocks.TagInfo;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.service.api.NaverTranslatorApiService;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import miraeassetmobile.backend.service.comparator.StockNewsListComparator;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        //가격이 0인 것이 있다면 에러처리
        if(price == 0){
            throw new ServiceException(ErrorCode.NOT_EXIST_STOCK_SYMBOL);
        }

        //display name없는 종목들이 가끔 있음
        String title = f.getDisplayName();
        if(title == null){
            title = f.getShortName();
        }
        if(title==null){
            title = f.getLongName();
        }


        return responseService.successHandler(
                StockDetailResponseDto.builder()
                        .symbol(f.getSymbol())
                        .stockTitle(title)
                        .price(price)
                        .changePrice(changePrice)
                        .changePercent(changePercent)
                        .tagInfo(
                                TagInfo.builder()
                                        .type(f.getQuoteType())
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

            if(price==0){ //가격이 0인 종목은 반영하지 않음
                continue;
            }


            //display name없는 종목들이 가끔 있음
            String title = f.getDisplayName();
            if(title == null){
                title = f.getShortName();
            }
            if(title==null){
                title = f.getLongName();
            }


            resultList.add(
                    SimilarStockInfo.builder()
                            .symbol(f.getSymbol())
                            .stockTitle(title)
                            .price(price)
                            .changePrice(changePrice)
                            .changePercent(changePercent)
                            .build()
            );
        }

        //맨 마지막 항목은 기존것
        FinanceQuote basic = fq.get(fq.size()-1);

        //display name없는 종목들이 가끔 있음
        String title = basic.getDisplayName();
        if(title == null){
            title = basic.getShortName();
        }
        if(title==null){
            title = basic.getLongName();
        }


        return responseService.successHandler(

                SimilarStockResponseDto.builder()
                        .totalData(resultList.size())
                        .stockTitle(title)
                        .stockInfoList(resultList)
                        .build()

        );




    }




    public BanklassResponseEntity getStockNews(String symbol, String lang, String num){

        List<StockNews> news = yhFinanceRapidApiService.getStockMarketNews(symbol);

//        Collections.sort(newsList, new StockNewsListComparator()); //결과값 최신순으로 정렬

        List<StockNewsTime> newsList = new ArrayList<>();

        for (StockNews s :news) {

            newsList.add(

                    StockNewsTime.builder()
                            .description(s.getDescription())
                            .guid(s.getGuid())
                            .pubDate(calculateTime(s.getPubDate()))
                            .link(s.getLink())
                            .title(s.getTitle())
                            .build()

            );

        }

        Collections.sort(newsList, new StockNewsListComparator()); //결과값 최신순으로 정렬

        List<StockNewsDto> result = new ArrayList<>();

        int idx = newsList.size();

        //갯수를 입력받아서 그 만큼만 넘겨줌
        if (!num.equals("all")) {
            try {
                //총 보유한 뉴스의 양보다 많은 값을 요청하면 반영되지 않도록함
                if (Integer.valueOf(num) < idx)
                    idx = Integer.valueOf(num);

            } catch (NumberFormatException ex) {
                throw new ServiceException(ErrorCode.WRONG_PARAM_NUMBER);
            }
        }


        if (lang.equals("ko")) {

            for (int i = 0; i < idx; i++) {

                StockNewsTime n = newsList.get(i);

                String date = changeTime(n.getPubDate(), "ko");

                result.add(

                        StockNewsDto.builder()
                                .title(naverTranslatorApiService.translateToKo(n.getTitle()))
                                .link(n.getLink())
                                .date(date)
                                .build()

                );


            }

        } else { //영어

            for (int i = 0; i < idx; i++) {

                StockNewsTime n = newsList.get(i);

                String date = changeTime(n.getPubDate(), "en");

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

                if(t.getStrongBuy()>0) {
                    recommendTrendGraphData.add(
                            RecommendTrendGraphData.builder()
                                    .id(STRONGBUY.getTypeName())
                                    .value(t.getStrongBuy())
                                    .color("#F58220")
                                    .build()
                    );
                }

                if(t.getBuy()>0) {
                    recommendTrendGraphData.add(
                            RecommendTrendGraphData.builder()
                                    .id(BUY.getTypeName())
                                    .value(t.getBuy())
                                    .color("#F0B26B")
                                    .build()
                    );
                }

                if(t.getHold()>0) {
                    recommendTrendGraphData.add(
                            RecommendTrendGraphData.builder()
                                    .id(HOLD.getTypeName())
                                    .value(t.getHold())
                                    .color("#84888B")
                                    .build()
                    );
                }

                if(t.getSell()>0) {
                    recommendTrendGraphData.add(
                            RecommendTrendGraphData.builder()
                                    .id(SELL.getTypeName())
                                    .value(t.getSell())
                                    .color("#8DC8E8")
                                    .build()
                    );
                }

                if(t.getStrongSell()>0) {
                    recommendTrendGraphData.add(
                            RecommendTrendGraphData.builder()
                                    .id(STRONGSELL.getTypeName())
                                    .value(t.getStrongSell())
                                    .color("#043B72")
                                    .build()
                    );
                }

                break;

            }

        }




        return responseService.successHandler(
                recommendTrendGraphData
        );

    }



    public long calculateTime(String pubDate) {

        try {

            //1. Date로 type변경
            //"Tue, 28 Mar 2023 01:31:00 +0000"
            //변경하는 법
            //https://stackoverflow.com/questions/32911677/what-is-date-format-of-eee-dd-mmm-yyyy-hhmmssz
            //https://www.tabnine.com/code/java/methods/java.text.DateFormat/parse
            // 패턴 리스트
            //http://www.java2s.com/ref/java/java-datetimeformatter-patterns.html
            // +0000 관련(서머타임 없는 걸로 아는데 1시간이 적용이 안되어서 문제되면 고쳐야함)
            // https://stackoverflow.com/questions/12305826/what-does-0000-mean-in-the-context-of-a-date-returned-by-the-twitter-api
            SimpleDateFormat curFormater = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ssZ", Locale.UK);
            Date pubDateTime = curFormater.parse(pubDate);

            //2. 현 시각을 구함
            Date today = new Date();

            //3. 현시각 - (기사가 올라간 시각) -> 초단위
            //https://coding-factory.tistory.com/737 : Date타입 연산법
            long sec = (today.getTime() - pubDateTime.getTime()) / 1000;


            return sec;

        }catch(ParseException e){
            throw new ServiceException(ErrorCode.API_SEVER_ERROR_RAPID_YHFINANCE);//parse에러도 결과를 보여줄 수 없으므로 그냥 에러 처리
        }


    }



    // 초를 ㅇㅇ분 전/ ㅇㅇ시간 전 / ㅇㅇ 일 전 : 형태로 변경
    public String changeTime(long second, String lang){

        String mins = lang.equals("ko")? "분 전" : "minutes ago";
        String hours = lang.equals("ko")? "시간 전" : "hours ago";
        String days = lang.equals("ko")? "일 전" : "days ago";
        String months = lang.equals("ko")? "달 전" : "months ago";
        String years = lang.equals("ko")? "년 전" : "years ago";



        //분
        long min = second/60;

        if(min>=60){ // 1시간을 넘어가는 범위 -> 시간 단위로 보여줘야함

            long hour = second/3600;


            if(hour>=24){ //하루를 넘어가는 범위 -> 일 단위로 보여야함

                long day = second/(24*60*60);

                if(day>=30){//30일을 넘어가면 -> 달 단위로...

                    long month = day/30;


                    if(month>=12){

                        long year = month/12;

                        return year + years;

                    }


                    return month+ months;

                }


                return day + days;

            }

            return hour + hours;

        }

        return min+ mins;




    }



    public BanklassResponseEntity getChartData(String range, String symbol){

        String interval = "";

        if(range.equals("1d")){
            interval = "15m";
        }else if(range.equals("5d")){
            interval = "15m";
        }else if(range.equals("3mo")){
            interval = "1d";
        }else if(range.equals("1y")){
            interval = "1wk";
        }else{ //5y라고 생각
            interval = "1mo";
        }


        FinanceSpark financeSpark = yhFinanceApiService.getFinanceSpark(interval,range, symbol);

        List<Long> timestamp = financeSpark.getTimestamp();
        List<Double> close = financeSpark.getClose();


        List<PriceData> priceData = new ArrayList<>();

        int minPrice = 999999;
        int maxPrice = 0;
        for(int i=0; i<timestamp.size(); i++){

            if(close.get(i) != null) { //null인 경우가 있으면 skip해버리기

                int v = (int) Math.ceil(close.get(i));

                if (v > maxPrice) maxPrice = v;
                if (v < minPrice) minPrice = v;

                priceData.add(
                        PriceData.builder()
                                .time(timestamp.get(i))
                                .price(close.get(i))
                                .build()
                );
            }

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


    public BanklassResponseEntity getCompanyInfo(String symbol){


        AssetProfile a = yhFinanceApiService.getAssetProfile(symbol);


        CompanyInfoResponseDto companyInfoResponseDto = CompanyInfoResponseDto.builder()
                .address(a.getAddress1()+", "+a.getCity()+", "+a.getState()+" "+a.getZip())
                .country(a.getCountry())
                .phoneNumber(a.getPhone())
                .website(a.getWebsite())
                .industry(a.getIndustry())
                .sector(a.getSector())
                .industry(a.getIndustry())
                .employees(a.getFullTimeEmployees())
                .ceo(a.getCompanyOfficers().get(0).getName())
                .businessSummary(a.getLongBusinessSummary())
                .build();

        return responseService.successHandler(
                companyInfoResponseDto
        );


    }


    public BanklassResponseEntity getStockInfo(String symbol){


        FinanceQuote f = yhFinanceApiService.getFinanceQuote(symbol);



        return responseService.successHandler(
                StockInfoResponseDto.builder()
                        .exchangeName(f.getFullExchangeName())
                        .fiftyTwoWeekHigh(f.getFiftyTwoWeekHigh())
                        .fiftyTwoWeekLow(f.getFiftyTwoWeekLow())
                        .fiftyTwoWeekHighChange(f.getFiftyTwoWeekHighChange())
                        .fiftyTwoWeekLowChange(f.getFiftyTwoWeekLowChange())
                        .epsCurrentYear(f.getEpsCurrentYear())
                        .typeDisp(f.getQuoteType())
                        .region(f.getRegion())
                        .financialCurrency(f.getFinancialCurrency())
                        .averageDailyVolume3Month(f.getAverageDailyVolume3Month())
                        .averageDailyVolume10Day(f.getAverageDailyVolume10Day())
                        .build()
        );
    }



    public BanklassResponseEntity getWatchedList(int count, String scrIds){


        List<FinanceQuote> watchedList = yhFinanceApiService.getWatchList(count, scrIds);

        List<WatchedStockInfo> resultList = new ArrayList<>();


        for(int i=0; i<watchedList.size(); i++){

            FinanceQuote f = watchedList.get(i);

            //display name없는 종목들이 가끔 있음
            String title = f.getDisplayName();
            if(title == null){
                title = f.getShortName();
            }
            if(title==null){
                title = f.getLongName();
            }

            double price = Math.round(f.getRegularMarketPrice()*100)/100.0;
            double changePrice = Math.round(f.getRegularMarketChange()*100)/100.0;
            double changePercent = Math.round(f.getRegularMarketChangePercent()*10)/10.0;

            //0인경우 반영하지 않음
            if(price == 0){
                i--;
                continue;
            }

            resultList.add(

                    WatchedStockInfo.builder()
                            .symbol(f.getSymbol())
                            .stockTitle(title)
                            .rank(i+1)
                            .price(price)
                            .changePercent(changePercent)
                            .changePrice(changePrice)
                            .build()


            );


        }

        return responseService.successHandler(

                WatchedListResponseDto.builder()
                        .totalData(resultList.size())
                        .watchedStockInfoList(resultList)
                        .build()

        );



    }

}
