package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

//pagenation을 위한 import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,Long> {

    //해당 학급이 가진 직업을 전부 조회함
    List<Job> findByClassId(Long classId);

    int countByClassId(Long classId);


    Optional<Job> findById(Long id); //null일 가능성이 없어 optional쓰지 않음



    void deleteById(Long id);


    boolean existsById(Long id);
}
