package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.StudentStock;
import miraeassetmobile.backend.domain.entity.TransactionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentStockRepository extends JpaRepository<StudentStock, Long> {

    //학생이 보유한 주식 전부 다 가져오기
    List<StudentStock> findByStudentId(Long studentId);

    //학생이 보유한 주식 페이지 방식으로 가져오기
    Page<StudentStock> findByStudentId(Long studentId, Pageable page); //출금 입금 따로 조회할경우

    int countByStudentId(Long studentId);

}
