package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.TransactionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDataRepository extends JpaRepository<TransactionData,Long> {

    Page<TransactionData> findByStudentId(Long studentId, Pageable page);

    Page<TransactionData> findByStudentIdAndFrom(Long studentId, String from, Pageable page); //출금 입금 따로 조회할경우


}
