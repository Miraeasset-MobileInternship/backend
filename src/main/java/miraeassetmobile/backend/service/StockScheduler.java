package miraeassetmobile.backend.service;

import lombok.extern.slf4j.Slf4j;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.entity.StockBatch;
import miraeassetmobile.backend.domain.entity.TrendingStocks;
import miraeassetmobile.backend.repository.StockBatchRepository;
import miraeassetmobile.backend.repository.StudentStockRepository;
import miraeassetmobile.backend.repository.TrendingStocksRepository;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component // 추가
@EnableAsync // 추가
public class StockScheduler {

    YhFinanceApiService yhFinanceApiService;
    TrendingStocksRepository trendingStocksRepository;
    StudentStockRepository studentStockRepository;
    StockBatchRepository stockBatchRepository;



    StockScheduler(StockBatchRepository stockBatchRepository, StudentStockRepository studentStockRepository, TrendingStocksRepository trendingStocksRepository, YhFinanceApiService yhFinanceApiService){
        this.yhFinanceApiService = yhFinanceApiService;
        this.trendingStocksRepository = trendingStocksRepository;
        this.studentStockRepository = studentStockRepository;
        this.stockBatchRepository = stockBatchRepository;
    }
    public String makeString(){

        TrendingByRegion trending = yhFinanceApiService.getTrendingByRegion();

        List<Symbol> symbolList = trending.getQuotes();

        String data = "";

        for (Symbol s :symbolList) {

            data += s.getSymbol() +",";

        }

        return data.substring(0, data.length()-1);

    }


    //특정 주기로 trending table을 update
//    @Scheduled(cron = "0 0 0/1 * * *")
    public void scheduleSavingTrendStock() {

        //존재하는 데이터 전부 삭제 -> 트렌딩 주식은 업데이트 방식이면 안됨
//        trendingStocksRepository.deleteAll();

        String symbols = makeString();


        List<FinanceQuote> savingList = yhFinanceApiService.getFinanceQuotes(symbols);

        System.out.println(savingList);

        for (FinanceQuote f :savingList) {

            String price = String.format("%.2f", f.getRegularMarketPrice());
            String changePrice = String.format("%.2f", f.getRegularMarketChange());
            String changePercent = String.format("%.1f", f.getRegularMarketChangePercent());

            int changeStatus = -1;

            if (f.getRegularMarketChange() >= 0) {

                changePrice = "+" + changePrice;
                changePercent = "+" + changePercent;
                changeStatus = 1;

                if (f.getRegularMarketChange() == 0) {
                    changeStatus = 0;
                }

            }

            boolean open = f.getMarketState().equals("REGULAR");


//            //존재하는 경우
//            if(trendingStocksRepository.existsBySymbol(f.getSymbol())){
//
//                TrendingStocks t = trendingStocksRepository.findBySymbol(f.getSymbol()).get();
//
//
//                TrendingStocks newT = t.updateInfo(price,changePrice,changePercent,changeStatus,f.getQuoteType(),f.getFullExchangeName(),f.getCustomPriceAlertConfidence(),open);
//
//                trendingStocksRepository.save(newT);
//
//
//                //존재 안하는 경우
//            }else{
//
//
//                TrendingStocks t = TrendingStocks.builder()
//                        .symbol(f.getSymbol())
//                        .title(f.getShortName())
//                        .price(price)
//                        .changePrice(changePrice)
//                        .changePercent(changePercent)
//                        .changeStatus(changeStatus)
//                        .tagType(f.getQuoteType())
//                        .tagMarket(f.getFullExchangeName())
//                        .tagConfidence(f.getCustomPriceAlertConfidence())
//                        .open(open)
//                        .build();
//
//                TrendingStocks trendingStocks = trendingStocksRepository.save(t);
//
//            }


            TrendingStocks t = TrendingStocks.builder()
                    .symbol(f.getSymbol())
                    .title(f.getShortName())
                    .price(price)
                    .changePrice(changePrice)
                    .changePercent(changePercent)
                    .changeStatus(changeStatus)
                    .tagType(f.getQuoteType())
                    .tagMarket(f.getFullExchangeName())
                    .tagConfidence(f.getCustomPriceAlertConfidence())
                    .open(open)
                    .build();

            try {
                trendingStocksRepository.save(t);

            }catch (Exception e){
                System.out.println(e.toString());
            }


        }

    }


    //find all stocks that student bought (but unique)
//    @Scheduled(cron = "0 0 0/1 * * *")
    public List<Object> findAllUnique(){

        //학생들이 보유한 주식을 UNIQUE하게 구하기
        List<Object> list = studentStockRepository.findAllStocksStudentHave();


        //요청을 위해 하나로 묶기
        String data = "";

        for (Object s : list) {
            data += s.toString() + ",";
        }

        //요청
        List<FinanceQuote> fList = yhFinanceApiService.getFinanceQuotes(data.substring(0, data.length()-1));

        for (FinanceQuote f :fList) {

            //존재함 ->업데이트
            if(stockBatchRepository.existsByStockSymbol(f.getSymbol())){

                StockBatch stockBatch = stockBatchRepository.findByStockSymbol(f.getSymbol()).get();


                StockBatch newStock = stockBatch.updateInfo(
                        f.getRegularMarketPrice(),f.getMarketState(),f.getQuoteType(),f.getFullExchangeName(),f.getCustomPriceAlertConfidence(),f.getRegularMarketChange(),f.getRegularMarketChangePercent()
                );

                stockBatchRepository.save(newStock);

            }else{

                String title = f.getDisplayName();
                if(title == null){
                    title = f.getShortName();
                }
                if(title==null){
                    title = f.getLongName();
                }


                stockBatchRepository.save(
                        StockBatch.builder()
                                .stockSymbol(f.getSymbol())
                                .title(title)
                                .regularMarketPrice(f.getRegularMarketPrice())
                                .marketStatus(f.getMarketState())
                                .fullExchangeName(f.getFullExchangeName())
                                .typeDisplay(f.getQuoteType())
                                .fullExchangeName(f.getFullExchangeName())
                                .customPriceAlertConfidence(f.getCustomPriceAlertConfidence())
                                .regularMarketChange(f.getRegularMarketChange())
                                .regularMarketChangePercent(f.getRegularMarketChangePercent())
                                .build()
                );

            }




        }

        return studentStockRepository.findAllStocksStudentHave();

    }





    //1분마다
    // cron = 초 분 시간 일 월 요일
    /*
    금/토요일에만 오후 10시부터 7시까지 1분간격으로 진행
     */
    //https://itworldyo.tistory.com/40
//    @Scheduled(cron = "0 0/1 0-7,22-23 * * 5,6")
//    public void scheduleTaskUsingCronExpression() {
//
//        String data = makeString();
//
//        //가장 최신순과 비교해서 달라진 경우 "====="를 넣기
//        TrendingEvery t = trendingEveryRepository.findTop1ByOrderByCreateTimestampDesc().orElse(
//                TrendingEvery.builder()
//                        .dataString("error")
//                        .build()
//        );
//
//        if(!t.getDataString().equals(data)){
//            trendingEveryRepository.save(
//                    TrendingEvery.builder()
//                            .dataString("======")
//                            .build()
//            );
//        }
//
//        //찾은값 저장
//        trendingEveryRepository.save(
//                TrendingEvery.builder()
//                        .dataString(data)
//                        .build()
//        );
//
//    }



}
