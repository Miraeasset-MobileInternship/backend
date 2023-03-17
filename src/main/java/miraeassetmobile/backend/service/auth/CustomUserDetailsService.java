package miraeassetmobile.backend.service.auth;

import lombok.RequiredArgsConstructor;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.UserInfoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/*
    Repository를 통해 database로부터 필요한 user 정보를 가져오는 service
*/
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNum) throws UsernameNotFoundException {

        UserInfo isExist = userInfoRepository.findByPhoneNum(phoneNum).orElseThrow( () ->
                new ServiceException(ErrorCode.INVALID_PHONENUMBER));


        return User.builder()
                .username(String.valueOf(isExist.getId()))
                .password("")
                .roles(isExist.getUserRole())
                .build();
    }
}