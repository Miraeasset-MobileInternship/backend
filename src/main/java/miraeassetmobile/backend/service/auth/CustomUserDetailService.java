package miraeassetmobile.backend.service.auth;

import lombok.RequiredArgsConstructor;
import miraeassetmobile.backend.domain.entity.UserInfo;
import org.springframework.security.core.userdetails.User;
import miraeassetmobile.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNum) throws UsernameNotFoundException {

        UserInfo u = userRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return User.builder()
                .username(u.getId().toString())
                .roles(u.getRole())
                .build();
    }

}
