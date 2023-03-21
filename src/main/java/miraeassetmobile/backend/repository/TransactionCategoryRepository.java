package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory,Long> {


    Optional<TransactionCategory> findById(Long id);


    /*
    Optional 은 컬렉션,스트림,배열,옵셔널 타입에서 사용해서는 안된다
    https://dahye-jeong.gitbook.io/java/java/effective_java/2021-07-12-return-optionals-judiciouly
     */
    List<TransactionCategory> findByTransferTrue();

    List<TransactionCategory> findByPayTrue();


    boolean existsByTransferTrueAndId(Long id);


    boolean existsByPayTrueAndId(Long id);
}
