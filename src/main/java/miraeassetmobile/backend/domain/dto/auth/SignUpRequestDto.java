package miraeassetmobile.backend.domain.dto.auth;

import lombok.*;
import miraeassetmobile.backend.domain.entity.UserInfo;
import reactor.util.annotation.Nullable;

@Getter
@Builder
@Setter
public class SignUpRequestDto {

    private String phoneNumber;

    private String userName;


    private String userRole; //선생님인지 학생인지


    public UserInfo toUser(String phoneNumber, String userName, String userRole){
        return UserInfo.builder()
                .name(userName)
                .phoneNum(phoneNumber)
                .role(userRole)
                .build();
    }



}
