package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.TestInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestInfo, Long> {


    TestInfo getById(int id);

    TestInfo findById(int id);

}
