package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.dto.stocks.TotalStockInfoResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.TrendStockDto;
import miraeassetmobile.backend.domain.dto.stocks.TrendingStockListDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.StudentStock;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.StudentStockRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    YhFinanceApiService yhFinanceApiService;
    ResponseService responseService;
    StudentRepository studentRepository;
    ClassRepository classRepository;
    StudentStockRepository studentStockRepository;

    StockService(YhFinanceApiService yhFinanceApiService, ResponseService responseService, StudentRepository studentRepository, ClassRepository classRepository,StudentStockRepository studentStockRepository){
        this.yhFinanceApiService = yhFinanceApiService;
        this.responseService = responseService;
        this.studentRepository =studentRepository;
        this.classRepository = classRepository;
        this.studentStockRepository = studentStockRepository;
    }


    //오늘의 trending 주식
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



    public TotalStockInfoResponseDto getTotalStockStatus(Long studentId){

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

            double price = financeQuote.getRegularMarketPrice() * 10 ; //현재 가격 * 10(미소단위 변환)

            // 보유 주식의 현 가격
//            double price = Double.parseDouble(stockApiResponseDto.getItems().get(0).getClpr()) * 10;  // 미소 단위로 변환 (1달러 = 10미소 = 1000원)


            // 결과가 string으로 api 에서 return되기 때문에 변경 해주어야함
            // 100 미소 == 10000원
            marketValue += price * ss.getAmount(); //가지고 있는 수량만큼 곱해줌


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



        return TotalStockInfoResponseDto.builder()
                .studentId(studentId)
                .money(money)
                .classCurrency(c.getCurrency())
                .totalMarketValue(marketValues)
                .totalBlendedPrice(blendedPrices)
                .totalMarketProfitLoss(marketProfitLosses)
                .totalYield(yieldValues)
                .build();

    }




}
