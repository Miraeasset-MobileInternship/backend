package miraeassetmobile.backend.repository;


import miraeassetmobile.backend.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByClassId(Long classId);



}
