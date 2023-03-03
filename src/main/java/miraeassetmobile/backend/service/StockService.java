package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.api.StockApiResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.TotalStockInfoResponseDto;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.StudentStock;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.StudentStockRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {


    StudentRepository studentRepository;
    StudentStockRepository studentStockRepository;

    StockApiCallService stockApiCallService;

    StockService(StudentRepository studentRepository, StudentStockRepository studentStockRepository, StockApiCallService stockApiCallService){
        this.studentRepository = studentRepository;
        this.studentStockRepository = studentStockRepository;
        this.stockApiCallService = stockApiCallService;
    }


    public TotalStockInfoResponseDto getTotalStockStatus(Long studentId){

        /*
        주의!
        지금 단위가 미소가 아님!!!
        "원"임 기획 회의 후 수정이 필요
         */



        Student s = studentRepository.findById(studentId).orElseThrow(()-> (new NotExistException(ErrorCode.NOT_EXIST_STUDENT)));

        List<StudentStock> studentStockList = studentStockRepository.findByStudentId(studentId);


        //보유금액
        int money = s.getMoney();


        //평가금액
        int marketValue = 0;

        /*
        이 학생이 산 모든 주식 코드 얻어오기

        해당 주식의 현 가격 * 보유량  (종가 : clpr ) api를 통해 조회하기
         */


        //학생이 보유하고 있는 모든 주식에 대하여
        for (StudentStock ss: studentStockList) {

            String code = ss.getStockCode(); //해당 주식의 코드

            StockApiResponseDto stockApiResponseDto = stockApiCallService.getStockInfoByCode(code).getBody();

            // 보유 주식의 현 가격
            int price = Integer.parseInt(stockApiResponseDto.getItems().get(0).getClpr()); // 결과가 string으로 api 에서 return되기 때문에 변경 해주어야함

            // 100 미소 == 10000원
            marketValue += price * ss.getAmount(); //가지고 있는 수량만큼 곱해줌

        }


        //매수금액
        int blendedPrice = 0;
        for (StudentStock ss: studentStockList) {
            blendedPrice += ss.getBlendedPrice() * ss.getAmount(); //보유한 모든 종목의 총 평균구매단가를 합하기
        }


        //평가손익 = 평가금액 - 매수금액
        int marketProfitLoss = marketValue - blendedPrice;


        //수익률
        double yield = 100.00 * marketProfitLoss / blendedPrice;
        String yieldValue = String.format("%.2f",yield); //두자리수 반올림



        return TotalStockInfoResponseDto.builder()
                .studentId(studentId)
                .money(money)
                .totalMarketValue(marketValue)
                .totalBlendedPrice(blendedPrice)
                .totalMarketProfitLoss(marketProfitLoss)
                .totalYield(yieldValue)
                .build();

    }












}
