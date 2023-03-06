package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassRepository extends JpaRepository<Classes,Long> {

    boolean existsById(Long id);

    Optional<Classes> findById(Long id);

    int countById(Long id);

}
