package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;


public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory,Long> {


    Optional<TransactionCategory> findById(Long id);


    List<TransactionCategory> findByTransferTrue();

    List<TransactionCategory> findByPayTrue();

}
