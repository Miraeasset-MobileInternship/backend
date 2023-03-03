package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.StudentStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentStockRepository extends JpaRepository<StudentStock, Long> {


    List<StudentStock> findByStudentId(Long studentId);


}
