package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.TransactionData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TransactionDataRepository extends JpaRepository<TransactionData,Long> {

    Page<TransactionData> findByStudentId(Long studentId, Pageable page);

    Page<TransactionData> findByStudentIdAndFrom(Long studentId, String from, Pageable page); //출금 입금 따로 조회할경우


    Page<TransactionData> findByClassId(Long classId, Pageable page);

    Page<TransactionData> findByClassIdAndFrom(Long classId, String from, Pageable page); //출금 입금 따로 조회할경우


    //최상단 거래를 조회(가장 최신의 거래)
    Optional<TransactionData> findTop1ByStudentIdOrderByCreateTimestampDesc(Long studentId);


    @Query(value = "SELECT * FROM transaction_data WHERE student_id=:studentId AND DATE(create_timestamp) < DATE(NOW()) ORDER BY create_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<TransactionData> findLastTransaction(Long studentId);

    @Query(value = "SELECT * FROM transaction_data WHERE class_id=:classId AND DATE(create_timestamp) < DATE(NOW()) ORDER BY create_timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<TransactionData> findLastTransactionClass(Long classId);


    int countByStudentId(Long studentId);

    int countByStudentIdAndAndFrom(Long studentId, String from);


    int countByClassId(Long classId);

    int countByClassIdAndAndFrom(Long classId, String from);

}
