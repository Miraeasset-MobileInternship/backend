package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.transactions.StudentTransactionDataDto;
import miraeassetmobile.backend.domain.dto.transactions.StudentTransactionResponseDto;
import miraeassetmobile.backend.domain.dto.transactions.TransactionCategoryDto;
import miraeassetmobile.backend.domain.entity.TransactionCategory;
import miraeassetmobile.backend.domain.entity.TransactionData;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.TransactionCategoryRepository;
import miraeassetmobile.backend.repository.TransactionDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TransactionService {

    TransactionDataRepository transactionDataRepository;
    TransactionCategoryRepository transactionCategoryRepository;
    StudentRepository studentRepository;


    TransactionService(TransactionCategoryRepository transactionCategoryRepository, TransactionDataRepository transactionDataRepository, StudentRepository studentRepository){
        this.studentRepository=studentRepository;
        this.transactionCategoryRepository=transactionCategoryRepository;
        this.transactionDataRepository=transactionDataRepository;
    }



    //학생별 거래내역 조회
    public StudentTransactionResponseDto getStudentTransactionData(Long studentId, int page){

        int pageSize = 10;


        Pageable pageable = PageRequest.of(page,pageSize, Sort.by("createTimestamp").descending()); //최신순
        Page<TransactionData> transactions = transactionDataRepository.findByStudentId(studentId, pageable);


        int totalData = transactionDataRepository.countByStudentId(studentId);


        int maxPage = (int) Math.ceil(totalData/(double)pageSize) -1; //요청가능한 마지막 페이지


        List<StudentTransactionDataDto> studentTransactionDatas = new ArrayList<>();

        for (TransactionData t: transactions) {


            boolean isDeposit = t.getFrom().equals("class"); //돈의 출처가 학생이면 출금


            StudentTransactionDataDto transaction = StudentTransactionDataDto.builder()
                    .transactionId(t.getId())
                    .category(transactionCategoryRepository.findById(t.getCategoryId()).get().getTitle())
                    .detail(t.getDetail())
                    .isDeposit(isDeposit)
                    .transactionMoney(t.getMoney())
                    .transactionDate(t.getCreateTimestamp().toLocalDateTime().toLocalDate())
                    .build();

            studentTransactionDatas.add(transaction);

        }

        return StudentTransactionResponseDto.builder()
                .currentPage(page)
                .totalData(totalData)
                .maxPage(maxPage)
                .studentTransactionData(studentTransactionDatas)
                .build();

    }


    public List<TransactionCategoryDto> getCategoryList(){

        List<TransactionCategory> categories = transactionCategoryRepository.findByChangeableTrue();

        List<TransactionCategoryDto> result = new ArrayList<>();

        for (TransactionCategory t: categories) {

            result.add(TransactionCategoryDto.builder()
                    .categoryId(t.getId())
                    .categoryTitle(t.getTitle())
                    .build());

        }

        return result;
    }



    //직업이 학생 계좌 출금(이체)권한을 가진 직업인가
    public void unavailableJobTransfer(Long jobId){
        if(!jobRepository.findById(jobId).get().isWithdrawStudent()){
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_JOB_TRANSFER);
        }
    }
}
