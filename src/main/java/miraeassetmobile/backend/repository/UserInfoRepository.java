package miraeassetmobile.backend.repository;


import miraeassetmobile.backend.domain.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {

    boolean existsByPhoneNum(String phoneNum);

    Optional<UserInfo> findByPhoneNum(String phoneNum);
    Optional<UserInfo> findById(Long id);


    //임시
    void deleteByPhoneNum(String phoneNum);

}
