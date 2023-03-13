package miraeassetmobile.backend.repository;

import miraeassetmobile.backend.domain.entity.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileImgRepository extends JpaRepository<ProfileImg,Long> {


    List<ProfileImg> findAll();


}
