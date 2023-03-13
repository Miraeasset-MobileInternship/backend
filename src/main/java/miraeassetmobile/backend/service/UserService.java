package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.users.ProfileImgListResponseDto;
import miraeassetmobile.backend.domain.dto.users.UserInfoResponseDto;
import miraeassetmobile.backend.domain.entity.ProfileImg;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ProfileImgRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    UserInfoRepository userInfoRepository;
    ProfileImgRepository profileImgRepository;



    UserService(UserInfoRepository userInfoRepository, ProfileImgRepository profileImgRepository){
        this.userInfoRepository = userInfoRepository;
        this.profileImgRepository = profileImgRepository;
    }



    public ProfileImgListResponseDto getProfileImgList(){

        return ProfileImgListResponseDto.builder()
                .profileImgList(profileImgRepository.findAll())
                .build();


    }


    public UserInfoResponseDto getUserName(Long userId){

        UserInfo user = userInfoRepository.findById(userId).orElseThrow(()-> new NotExistException(ErrorCode.NOT_EXSIT_USER));
        ProfileImg p = profileImgRepository.findById(user.getProfileImgId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_IMAGE));

        return UserInfoResponseDto.builder()
                .userName(user.getUserName())
                .profileImg(p.getIconCode())
                .build();
    }


    public UserInfo getUser(Long userId){

        UserInfo u = userInfoRepository.findById(userId).get();

        System.out.println(u.getUserName());

        return u;
    }

}
