package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory,Long> {


    Optional<TransactionCategory> findById(Long id);


}
