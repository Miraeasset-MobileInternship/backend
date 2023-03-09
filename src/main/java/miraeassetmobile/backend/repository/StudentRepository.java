package miraeassetmobile.backend.repository;


import miraeassetmobile.backend.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByClassId(Long classId);


    Optional<Student> findById(Long id);

    boolean existsById(Long id);


    List<Student> findByUserId(Long userId);




}
