package miraeassetmobile.backend.service;

import lombok.extern.slf4j.Slf4j;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.entity.TrendingEvery;
import miraeassetmobile.backend.repository.TrendingEveryRepository;
import miraeassetmobile.backend.service.api.YhFinanceApiService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Component // 추가
@EnableAsync // 추가
public class StockScheduler {

    YhFinanceApiService yhFinanceApiService;
    TrendingEveryRepository trendingEveryRepository;


    StockScheduler(YhFinanceApiService yhFinanceApiService, TrendingEveryRepository trendingEveryRepository){
        this.yhFinanceApiService = yhFinanceApiService;
        this.trendingEveryRepository = trendingEveryRepository;
    }

    public String makeString(){

        TrendingByRegion trending = yhFinanceApiService.getTrendingByRegion();

        List<Symbol> symbolList = trending.getQuotes();

        String data = "";

        for (Symbol s :symbolList) {

            data += s.getSymbol();

        }

        return data;

    }


    //1분마다
    // cron = 초 분 시간 일 월 요일
    /*
    토요일에만 0시부터 6시까지 1분간격으로 진행
     */
    @Scheduled(cron = "0 0/1 0-6 * * 6")
    public void scheduleTaskUsingCronExpression() {

        String data = makeString();

        //가장 최신순과 비교해서 달라진 경우 "====="를 넣기
        TrendingEvery t = trendingEveryRepository.findTop1ByOrderByCreateTimestampDesc().orElse(
                TrendingEvery.builder()
                        .dataString("error")
                        .build()
        );

        if(!t.getDataString().equals(data)){
            trendingEveryRepository.save(
                    TrendingEvery.builder()
                            .dataString("======")
                            .build()
            );
        }

        //찾은값 저장
        trendingEveryRepository.save(
                TrendingEvery.builder()
                        .dataString(data)
                        .build()
        );

    }

    @Scheduled(cron = "0/5 * 16 * * *")
    public void scheduleTaskUsingCronExpressionTest() {

        String data = makeString();

        //가장 최신순과 비교해서 달라진 경우 "====="를 넣기
        TrendingEvery t = trendingEveryRepository.findTop1ByOrderByCreateTimestampDesc().orElse(
                TrendingEvery.builder()
                        .dataString("error")
                        .build()
        );

        if(!t.getDataString().equals(data)){
            trendingEveryRepository.save(
                    TrendingEvery.builder()
                            .dataString("======")
                            .build()
            );
        }

        //찾은값 저장
        trendingEveryRepository.save(
                TrendingEvery.builder()
                        .dataString(data)
                        .build()
        );

    }

}
