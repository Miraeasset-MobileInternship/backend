package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Classes,Long> {

    boolean existsById(Long id);

    Optional<Classes> findById(Long id);

    int countById(Long id);

    List<Classes> findByTeacherId(Long teacherId);

    //같은 학교에 같은 나라이름이 존재해서는 안됨
    boolean existsBySchoolNameAndTitle(String schoolName, String title);




    //
    @Query(value = "select * from class where school_name=:schoolName and grade=:grade and class_number=:classNumber and YEAR(create_timestamp) =:year", nativeQuery = true)

    Optional<Classes> findSameClassInYear(String schoolName, int grade, int classNumber, String year);



}
