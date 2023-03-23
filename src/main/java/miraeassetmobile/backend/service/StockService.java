package miraeassetmobile.backend.service;

import lombok.extern.slf4j.Slf4j;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.AutoComplete;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.dto.stocks.*;
import miraeassetmobile.backend.domain.entity.*;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.*;
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

import static miraeassetmobile.backend.domain.enums.TransactionFromTypes.CLASS;
import static miraeassetmobile.backend.domain.enums.TransactionFromTypes.SELL;
import static org.hibernate.internal.CoreLogging.logger;

@Service
@Slf4j
public class StockService {

    YhFinanceApiService yhFinanceApiService;
    ResponseService responseService;
    YhFinanceRapidApiService yhFinanceRapidApiService;
    NaverTranslatorApiService naverTranslatorApiService;
    StudentRepository studentRepository;
    ClassRepository classRepository;
    StudentStockRepository studentStockRepository;
    StockTradingDataRepository stockTradingDataRepository;
    TransactionCategoryRepository transactionCategoryRepository;
    TransactionDataRepository transactionDataRepository;

    StockService(TransactionDataRepository transactionDataRepository, TransactionCategoryRepository transactionCategoryRepository, StockTradingDataRepository stockTradingDataRepository, NaverTranslatorApiService naverTranslatorApiService, YhFinanceRapidApiService yhFinanceRapidApiService, YhFinanceApiService yhFinanceApiService, ResponseService responseService, StudentRepository studentRepository, ClassRepository classRepository,StudentStockRepository studentStockRepository){
        this.yhFinanceApiService = yhFinanceApiService;
        this.responseService = responseService;
        this.studentRepository =studentRepository;
        this.classRepository = classRepository;
        this.studentStockRepository = studentStockRepository;
        this.yhFinanceRapidApiService = yhFinanceRapidApiService;
        this.naverTranslatorApiService= naverTranslatorApiService;
        this.stockTradingDataRepository = stockTradingDataRepository;
        this.transactionDataRepository = transactionDataRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
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



//    public BanklassResponseEntity getSearchAutoComplete(String query){
//
//
//        List<AutoComplete> autoComplete = yhFinanceApiService.getAutoComplete(query);
//
//
////        List<AutoCompleteResponseDto> result = new ArrayList<>();
//
////        for (AutoComplete a :autoComplete) {
////
////            FinanceQuote f = yhFinanceApiService.getFinanceQuote(a.getSymbol());
////
////            boolean open = f.getMarketState().equals("REGULAR");
////
////            TagInfo tagInfo = TagInfo.builder()
////                    .type(f.getTypeDisp())
////                    .market(f.getFullExchangeName())
////                    .customPriceConfidence(f.getCustomPriceAlertConfidence())
////                    .isOpen(open)
////                    .build();
////
////
////            result.add(AutoCompleteResponseDto.builder()
////                    .stockId(a.getSymbol())
////                    .stockTitle(f.getShortName())
////                    .tagInfo(tagInfo)
////                    .build());
////
////        }
//
//        return responseService.successHandler(autoComplete);
//
//    }


    public BanklassResponseEntity getSearchAutoComplete(String query){

        List<AutoComplete> autoComplete = yhFinanceApiService.getAutoComplete(query);

        return responseService.successHandler(
                AutoCompleteResponseDto.builder()
                        .totalData(autoComplete.size())
                        .autoCompleteList(autoComplete)
                        .build()
        );

    }


    public BanklassResponseEntity getMarketNews(String lang) throws ParseException {


        List<MarketNews> newsList = yhFinanceRapidApiService.getMarketNews();

        Collections.sort(newsList, new ListComparator()); //결과값 최신순으로 정렬

        List<MarketNewsDto> result = new ArrayList<>();

        if(lang.equals("ko")) {

            for (MarketNews n : newsList) {

                String date = changeTime(calculateTime(n.getPubDate()),"ko");

                result.add(

                        MarketNewsDto.builder()
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

                        MarketNewsDto.builder()
                                .title(n.getTitle())
                                .link(n.getLink())
                                .source(n.getSource())
                                .date(date)
                                .build()

                );


            }


        }

        return responseService.successHandler(
                MarketNewsResponseDto.builder()
                        .totalData(result.size())
                        .marketNewsList(result)
                        .build()
        );



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



    public BanklassResponseEntity checkBeforeSelling(String symbol, Long studentId){

        //존재하지 않는 학생
        Student s = studentRepository.findById(studentId).orElseThrow(()->new ServiceException(ErrorCode.NOT_EXIST_STUDENT));
        Classes c = classRepository.findById(s.getClassId()).orElseThrow(()->new ServiceException(ErrorCode.NOT_EXIST_CLASS));

        //조회
        StudentStock ss = studentStockRepository.findByStudentIdAndStockSymbol(studentId,symbol)
                .orElseThrow(()-> new ServiceException(ErrorCode.NOT_OWNED_STOCK)); //사용자가 보유하지 않은 주식이 여기서 체크되기 때문에 맨 위에 체크가 필요 없을 것 같음


        FinanceQuote f = yhFinanceApiService.getFinanceQuote(ss.getStockSymbol());

        String marketPrice = String.format("%.1f", f.getRegularMarketPrice());

        int price = (int) Math.floor(Double.parseDouble(marketPrice)); // marketPrice를 내림해서 int로

        return responseService.successHandler(

                CheckForSellingStockResponseDto.builder()
                        .stockId(ss.getStockSymbol())
                        .stockTitle(f.getShortName())
                        .marketPrice(marketPrice)
                        .price(price)
                        .availableAmount(ss.getAmount())
                        .currency(c.getCurrency())
                        .build()

        );

    }



    @Transactional
    public BanklassResponseEntity sellshares(StockSellingRequestDto stockSellingRequestDto){

        //데이터 꺼내두기
        String stockSymbol = stockSellingRequestDto.getStockId();
        int price = stockSellingRequestDto.getPrice();
        Long studentId = stockSellingRequestDto.getStudentId();
        int sellingAmount = stockSellingRequestDto.getAmount();




        //존재하는 학생인가
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));

        //보유한 종목이 맞는가
        StudentStock ss = studentStockRepository.findByStudentIdAndStockSymbol(studentId, stockSymbol)
                .orElseThrow(()-> new ServiceException(ErrorCode.NOT_OWNED_STOCK));

        //유효한 종목인가 (Finance api로)
        FinanceQuote stockInfo = yhFinanceApiService.getFinanceQuote(stockSymbol);




        //거래가 가능함


            //
//        /*
//        **국고와는 관계가 없다.
//
//        0. 학생잔고에 돈 추가하기 money update
//        1. student_stock 테이블에 보유 수량 체크하고 0개면 삭제하기
//        2. stock_trading_data 테이블에 거래 내역 저장하기
//        3. transaction_data 테이블에 거래 내역 저장하기
//            * manager정보 -1로 처리
//            * 태그 주식으로 지정하기
//
//         */

            //0. 학생잔고에 돈 추가하기
            //" 판매금액 * 보유수량 "
            int updatedStudentMoney = updateSellingStudentMoney(studentId, price * sellingAmount);


            //1. student stock table에서 보유수량 변경
            /*

            1. 판매하려고 하는 수량이 보유수량보다 많은지 확인
            2. 판매하려는 수량만큼 감소시킴
            3. 만약 0개 이하인 경우 테이블에서 삭제함

             */
            updateAmountOfShares(ss, sellingAmount);


            //2. stock trading table에 추가하기
            try {
                StockTradingData stockTradingData = stockSellingRequestDto.toStockTradingData(studentId, stockSymbol, sellingAmount, price);

                stockTradingDataRepository.save(stockTradingData);
            }catch (Exception e){
                log.error("Exception : "+"Stock Trading Data Table에 넣는 과정에서 생긴 에러");
                throw new ServiceException(ErrorCode.TRADING_STOCK);
            }




            //3. transaction_data 테이블에 거래 내역저장하기


            String detailMessage = stockInfo.getDisplayName()+" "+sellingAmount+"주 매도";

            Long categoryId = 8L;

            try {
                TransactionData transactionData = transactionDataRepository.save(

                        TransactionData.builder()
                                .money(price * sellingAmount) // 구매가격 * 구매수량
                                .studentMoney(updatedStudentMoney) //거래 후 남은 학생 잔고
                                .classMoney(-1)//관계없음
                                .managerId(-1L)//존재하지 않음
                                .managerJobId(-1L) //존재하지 않음
                                .studentId(studentId)//본인
                                .studentJobId(student.getJobId())//본인직업
                                .classId(student.getClassId())//학생 소속 반
                                .categoryId(categoryId) //number 8 : 투자
                                .detail(detailMessage)//매도 매수
                                .from(SELL.getTypeName()) //class 학급잔고에서 나온 돈은 아니지만 학생계좌로 돈이 들어가는 것 이므로..
                                .build()

                );

                return responseService.successHandler(
                        CreatedUriDto.builder()
                                .status("created")
                                .url(responseService.createUri(transactionData.getId(), UriTypes.TRANSACTION))
                                .build()
                );


            }catch(Exception e){
                log.error("Exception : "+"Transaction data table에 넣는 과정에서 생긴 에러");
                throw new ServiceException(ErrorCode.TRADING_STOCK);
            }



    }

//    public BanklassResponseEntity checkPriceByStockId(String stockSymbol){
//
//        //종목 조회
//        FinanceQuote stockInfo = yhFinanceApiService.getFinanceQuote(stockSymbol);
//
//        String price = String.format("%.2f",stockInfo.getRegularMarketPrice()); // 가격 미소로 변환 후 2자리까지
//
//
//
//
//    }



//    @Transactional
//    public BanklassResponseEntity buyStock(StockBuyingRequestDto stockBuyingRequestDto){
//
//        //존재하는 학생인가
//        Student student = studentRepository.findById(stockBuyingRequestDto.getStudentId())
//                .orElseThrow(()-> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));
//
//        //종목 조회
//        FinanceQuote stockInfo = yhFinanceApiService.getFinanceQuote(stockBuyingRequestDto.getStockId());
//
//       /*
//        여기소수점 어떻게 하기로 했는지 까먹음..;
//
//        아마 살때는 : 내림
//        팔때는 : 올림..
//
//         */
//
//        //미소 단위로 변환 + 소수점 처리(내림)
//        int price = (int) Math.floor(stockInfo.getRegularMarketPrice()); //미소 단위로 변환
//
//
//        //주문 금액이 부족한지 체크
//        responseService.notEnoughMoneyForBuyingStock(stockBuyingRequestDto.getStudentId(), price * stockBuyingRequestDto.getAmount());
//
//
//
//        //거래가 가능함
//
//        /*
//        **국고와는 관계가 없다.
//
//        0. 학생잔고에서 돈 빼기 money update
//        1. student_stock 테이블에 보유 수량 저장하기 (존재하지 않으면 새로 생성)
//                * 평균구매단가 수정해야함!!
//        2. stock_trading_data 테이블에 거래 내역 저장하기
//        3. transaction_data 테이블에 거래 내역 저장하기
//            * manager정보 -1로 처리
//            * 태그 주식으로 지정하기
//
//         */
//
//
//
//    }


    @Transactional
    public int updateSellingStudentMoney(Long studentId, int money){

        try {

            Student student = studentRepository.findById(studentId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));

            //객체의 돈을 변경하여 새로운 객체를 생성
            Student updateStudent = student.updateMoney(student.getMoney() + money); //보유하고 있는 금액 + 매도한 금액

            studentRepository.save(updateStudent);

            return updateStudent.getMoney();

        }catch(Exception e){
            log.error("Exception : "+"Student money 업데이트 과정에서 생긴 에러");
            throw new ServiceException(ErrorCode.TRADING_STOCK);
        }
    }

    @Transactional
    public int updateBuyingStudentMoney(Long studentId, int money) {

        try {
            Student student = studentRepository.findById(studentId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));

            //객체의 돈을 변경하여 새로운 객체를 생성
            Student updateStudent = student.updateMoney(student.getMoney() - money); //보유금액 - 매수금액

            studentRepository.save(updateStudent);

            return updateStudent.getMoney();

        }catch (Exception e){
            log.error("Exception : "+"Student money 업데이트 과정에서 생긴 에러");
            throw new ServiceException(ErrorCode.TRADING_STOCK);
        }
    }

    //판매해서 보유 테이블을 수정
    @Transactional
    public void updateAmountOfShares(StudentStock stockStock, int sellingAmount) {


        //팔려고하는 수량이 보유 수량보다 적은게 확실한가
        if(sellingAmount > stockStock.getAmount()){
            // 판매시도 수량 > 보유 수량 인 경우 에러
            throw new ServiceException(ErrorCode.NOT_ENOUGH_SHARES);
        }


        try {
            //객체의 보유수량을 변경하여 새로운 객체를 생성
            StudentStock updatedStudentStock = stockStock.updateAmount(stockStock.getAmount() - sellingAmount);

            //만약에 보유 수량이 0개 이하면 해당 row삭제
            if (updatedStudentStock.getAmount() <= 0) {
                studentStockRepository.deleteById(updatedStudentStock.getId());
            } else {        //아니면 그냥 업데이트만
                studentStockRepository.save(updatedStudentStock);
            }
        }catch(Exception e){
            log.error("Exception : "+"Student stock table을 수정하는 과정에서 에러");
            throw new ServiceException(ErrorCode.TRADING_STOCK);
        }


    }





}
