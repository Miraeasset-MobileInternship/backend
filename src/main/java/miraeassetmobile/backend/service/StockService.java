package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.AutoComplete;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.dto.stocks.*;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.StudentStock;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.StudentStockRepository;
import miraeassetmobile.backend.service.api.NaverTranslatorApiService;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import miraeassetmobile.backend.service.api.YhFinanceRapidApiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    YhFinanceApiService yhFinanceApiService;
    ResponseService responseService;
    YhFinanceRapidApiService yhFinanceRapidApiService;
    NaverTranslatorApiService naverTranslatorApiService;
    StudentRepository studentRepository;
    ClassRepository classRepository;
    StudentStockRepository studentStockRepository;

    StockService(NaverTranslatorApiService naverTranslatorApiService, YhFinanceRapidApiService yhFinanceRapidApiService, YhFinanceApiService yhFinanceApiService, ResponseService responseService, StudentRepository studentRepository, ClassRepository classRepository,StudentStockRepository studentStockRepository){
        this.yhFinanceApiService = yhFinanceApiService;
        this.responseService = responseService;
        this.studentRepository =studentRepository;
        this.classRepository = classRepository;
        this.studentStockRepository = studentStockRepository;
        this.yhFinanceRapidApiService = yhFinanceRapidApiService;
        this.naverTranslatorApiService= naverTranslatorApiService;
    }


    //오늘의 trending 주식
    public BanklassResponseEntity getTodayTrending(){


        TrendingByRegion trending = yhFinanceApiService.getTrendingByRegion();

        List<Symbol> symbolList = trending.getQuotes();

        List<TrendStockDto> trendList = new ArrayList<>();


        for (Symbol s: symbolList) {

            String symbolCode = s.getSymbol();

            FinanceQuote f = yhFinanceApiService.getFinanceQuote(symbolCode);

            String price = String.format("%.1f",f.getRegularMarketPrice()); // 미소로 변환
            String change = String.format("%.1f",f.getRegularMarketChange());
            String changePercent = String.format("%.1f",f.getRegularMarketChangePercent());

            boolean open = f.getMarketState().equals("REGULAR");


            TagInfo tagInfo = TagInfo.builder()
                    .type(f.getTypeDisp())
                    .market(f.getFullExchangeName())
                    .customPriceConfidence(f.getCustomPriceAlertConfidence())
                    .isOpen(open)
                    .build();


            trendList.add(TrendStockDto.builder()
                    .stockId(f.getSymbol())
                    .stockTitle(f.getShortName())
                    .price(price)
                    .change(change)
                    .changePercent(changePercent)
                    .tagInfo(tagInfo)
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



    public BanklassResponseEntity getTotalStockStatus(Long studentId){

        Student s = studentRepository.findById(studentId).orElseThrow(()-> (new ServiceException(ErrorCode.NOT_EXIST)));
        Classes c = classRepository.findById(s.getClassId()).orElseThrow(()-> (new ServiceException(ErrorCode.NOT_EXIST)));


        List<StudentStock> studentStockList = studentStockRepository.findByStudentId(studentId);



        //보유금액 (단위 이름 붙여서)
        int money = s.getMoney();



        //평가금액
        double marketValue = 0;

        /*
        이 학생이 산 모든 주식 코드 얻어오기
        해당 주식의 현 가격 * 보유량  (종가 : clpr ) api를 통해 조회하기
         */


        //학생이 보유하고 있는 모든 주식에 대하여
        for (StudentStock ss: studentStockList) {

            String symbol = ss.getStockSymbol(); //해당 주식의 symbol

            /*
            여기서 에러처리 한번 들어가야함
            1. 그냥 정상 제외 모든 에러들 -> serverError
            2. 결과가 없을 경우: list가 빈 리스트라 여기 안들어와질 것 같은데 (보유한 주식이 없는 경우)
             */

            FinanceQuote financeQuote = yhFinanceApiService.getFinanceQuote(symbol);

            String price = String.format("%.1f",financeQuote.getRegularMarketPrice()) ; //현재 가격

            // 보유 주식의 현 가격
//            double price = Double.parseDouble(stockApiResponseDto.getItems().get(0).getClpr()) * 10;  // 미소 단위로 변환 (1달러 = 10미소 = 1000원)


            // 결과가 string으로 api 에서 return되기 때문에 변경 해주어야함
            // 100 미소 == 10000원
            marketValue += Double.parseDouble(price) * ss.getAmount(); //가지고 있는 수량만큼 곱해줌


        }



        //매수금액
        double blendedPrice = 0;
        for (StudentStock ss: studentStockList) {

            //평단가 가져와서 다 더하기
            double blend = ss.getBlendedPrice().doubleValue();

            blendedPrice += blend * ss.getAmount();


        }


        //평가손익 = 평가금액 - 매수금액
        double marketProfitLoss = marketValue - blendedPrice;


        //수익률 = (손익)/(투자원금=매수금액) * 100
        double yield = marketProfitLoss/blendedPrice  * 100;



        //전부 두자리수 까지만 (반올림)
        String yieldValues = Double.isNaN(yield)? "0.00" : String.format("%.2f",yield); //Nan인 경우 0.00으로
        String marketValues = String.format("%.2f",marketValue);
        String blendedPrices = String.format("%.2f",blendedPrice);
        String marketProfitLosses = String.format("%.2f",marketProfitLoss);



        return responseService.successHandler(
                TotalStockInfoResponseDto.builder()
                .studentId(studentId)
                .money(money)
                .classCurrency(c.getCurrency())
                .totalMarketValue(marketValues)
                .totalBlendedPrice(blendedPrices)
                .totalMarketProfitLoss(marketProfitLosses)
                .totalYield(yieldValues)
                .build()
        );

    }




    public BanklassResponseEntity getOwnedStockList(Long studentId, int page){

        Student s = studentRepository.findById(studentId).orElseThrow(()-> (new ServiceException(ErrorCode.NOT_EXIST)));
        Classes c = classRepository.findById(s.getClassId()).orElseThrow(()-> (new ServiceException(ErrorCode.NOT_EXIST)));


        int totalData = studentStockRepository.countByStudentId(studentId);

        if(totalData == 0){
            return responseService.successHandler(
                    OwnStockInfoResponseDto.builder()
                            .totalData(0)
                            .currentPage(0)
                            .maxPage(0)
                            .classCurrency(c.getCurrency())
                            .stockInfoList(new ArrayList<>())
                            .build()
            );
        }

        int pageSize = 10;

        int maxPage = (int) Math.ceil(totalData/(double)pageSize) -1; //요청가능한 마지막 페이지

        Pageable pageable = PageRequest.of(page,pageSize);

        //보유한 종목 리스트
        Page<StudentStock> ss = studentStockRepository.findByStudentId(studentId, pageable);

        List<OwnStockInfo> ownStockInfos = new ArrayList<>();

        for (StudentStock stock: ss) {

            //해당 주식 심볼로 검색
            FinanceQuote f = yhFinanceApiService.getFinanceQuote(stock.getStockSymbol());

            //현재가 (미소전환)
            double crPrice = f.getRegularMarketPrice(); //
            String price = String.format("%.1f",crPrice);


            //평균구매단가 (1개 기준)
            double blPrice = stock.getBlendedPrice().doubleValue();
            String blendedPrice = String.format("%.1f", blPrice);



            //평가손익 = 현재금액(현재가) - 매수금액(내가 지불한 금액)
            // 미소단위로 변환된 현재가 - 미소단위로 db에 저장되어 있는 평균구매단가 = 평가손익
            double mProfitLoss = crPrice - blPrice;
            String marketProfitLoss = String.format("%.1f", mProfitLoss * stock.getAmount());// 보유수량 곱해줘야함 !

            //수익률 = (손익)/(투자원금=매수금액) * 100
            double y = mProfitLoss/blPrice  * 100;
            String yield = String.format("%.1f", y);



            //태그 작성
            boolean open = f.getMarketState().equals("REGULAR");

            TagInfo tagInfo = TagInfo.builder()
                    .type(f.getTypeDisp())
                    .market(f.getFullExchangeName())
                    .customPriceConfidence(f.getCustomPriceAlertConfidence())
                    .isOpen(open)
                    .build();


            ownStockInfos.add(

                    OwnStockInfo.builder()
                            .stockId(stock.getStockSymbol()) // symbol
                            .stockTitle(f.getShortName())
                            .price(price)
                            .count(stock.getAmount()) // 보유수량
                            .blendedPrice(blendedPrice) // 평균구매단가
                            .marketProfitLoss(marketProfitLoss)
                            .yield(yield)
                            .tagInfo(tagInfo)
                            .build()

            );

        }




        return responseService.successHandler(
                OwnStockInfoResponseDto.builder()
                        .totalData(totalData)
                        .currentPage(page)
                        .maxPage(maxPage)
                        .classCurrency(c.getCurrency())
                        .stockInfoList(ownStockInfos)
                        .build()
        );

    }



    public BanklassResponseEntity getSearchAutoComplete(String query){


        List<AutoComplete> autoComplete = yhFinanceApiService.getAutoComplete(query);


        List<AutoCompleteResponseDto> result = new ArrayList<>();

        for (AutoComplete a :autoComplete) {

            FinanceQuote f = yhFinanceApiService.getFinanceQuote(a.getSymbol());

            boolean open = f.getMarketState().equals("REGULAR");

            TagInfo tagInfo = TagInfo.builder()
                    .type(f.getTypeDisp())
                    .market(f.getFullExchangeName())
                    .customPriceConfidence(f.getCustomPriceAlertConfidence())
                    .isOpen(open)
                    .build();


            result.add(AutoCompleteResponseDto.builder()
                    .stockId(a.getSymbol())
                    .stockTitle(f.getShortName())
                    .tagInfo(tagInfo)
                    .build());

        }

        return responseService.successHandler(result);

    }



    public BanklassResponseEntity getMarketNews(String lang) throws ParseException {


        List<MarketNews> newsList = yhFinanceRapidApiService.getMarketNews();

        List<MarketNewsResponseDto> result = new ArrayList<>();

        if(lang.equals("ko")) {

            for (MarketNews n : newsList) {

                String date = changeTime(calculateTime(n.getPubDate()),"ko");

                result.add(

                        MarketNewsResponseDto.builder()
                                .title(naverTranslatorApiService.translateToKo(n.getTitle()))
                                .link(n.getLink())
                                .source(n.getSource())
                                .date(date)
                                .build()

                );


            }

        }else{ //영어

            for (MarketNews n : newsList) {

                String date = changeTime(calculateTime(n.getPubDate()),"en");

                result.add(

                        MarketNewsResponseDto.builder()
                                .title(n.getTitle())
                                .link(n.getLink())
                                .source(n.getSource())
                                .date(date)
                                .build()

                );


            }


        }

        return responseService.successHandler(result);



    }



    public long calculateTime(String pubDate) throws ParseException {

        //1. LocalDateTime으로 type변경
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDateTime pubDateTime = LocalDateTime.parse(pubDate, formatter);


        //2. 시간 변경
        /*
        실제로 넘어오는 시간이 +0 timezone기준인데,
        한국 타임존(+9)으로 자동으로 설정되므로 -9시간 빼줘야함
         */
        LocalDateTime krPubDateTime = pubDateTime.minusHours(9);


        //2. 현 시각을 구함
        LocalDateTime today = LocalDateTime.now();


        //3. 현시각 - (기사가 올라간 시각) -> 초단위
        Duration duration = Duration.between(krPubDateTime, today);


        long sec = duration.getSeconds();


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





    public long test(String date) throws ParseException {

        System.out.println("start");

        return calculateTime(date);

    }


}
