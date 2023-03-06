package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.api.StockApiResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.StudentStockInfoResponseDto;
import miraeassetmobile.backend.domain.dto.stocks.TotalStockInfoResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.StudentStock;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.StudentStockRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {


    StudentRepository studentRepository;
    StudentStockRepository studentStockRepository;
    ClassRepository classRepository;

    StockApiCallService stockApiCallService;

    StockService(ClassRepository classRepository, StudentRepository studentRepository, StudentStockRepository studentStockRepository, StockApiCallService stockApiCallService){
        this.studentRepository = studentRepository;
        this.studentStockRepository = studentStockRepository;
        this.stockApiCallService = stockApiCallService;
        this.classRepository = classRepository;
    }


    public TotalStockInfoResponseDto getTotalStockStatus(Long studentId){

        /*
        주의!
        지금 단위가 미소가 아님!!!
        "원"임 기획 회의 후 수정이 필요
         */



        Student s = studentRepository.findById(studentId).orElseThrow(()-> (new NotExistException(ErrorCode.NOT_EXIST_STUDENT)));
        Classes c = classRepository.findById(s.getClassId()).orElseThrow(() -> (new NotExistException(ErrorCode.NOT_EXIST_CLASS)));


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

            String code = ss.getStockCode(); //해당 주식의 코드

            /*

            여기서 에러처리 한번 들어가야함
            API쪽에서 에러났을 경우 -> stockApiCallSerivce에서 한번... 헤더 뜯어서 상태코드 몇가지로 처리해야할듯

            1. 그냥 정상 제외 모든 에러들 -> api에러라고 말하기
            2. 결과가 없을 경우 저장된 코드에러라고 말하기

             */

            StockApiResponseDto stockApiResponseDto = stockApiCallService.getStockInfoByCode(code).getBody();
            // 보유 주식의 현 가격
            double price = Double.parseDouble(stockApiResponseDto.getItems().get(0).getClpr()) * 0.01;  // 미소 단위로 변환


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



        //전부 두자리수에서 끊어서
        String yieldValues = String.format("%.2f",yield); //두자리수 반올림
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



//    public List<StudentStockInfoResponseDto> getStudentStockList(Long studentId){
//
//
//
//        return
//
//    }












}
