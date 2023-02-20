package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

//pagenation을 위한 import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface JobRepository extends JpaRepository<Job,Long> {

    //해당 학급이 가진 직업을 전부 조회함
    Page<Job> findByClassId(int classId, Pageable pageable);

    int countByClassId(int classId);

}
