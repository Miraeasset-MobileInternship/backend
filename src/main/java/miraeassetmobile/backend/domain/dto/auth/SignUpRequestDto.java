package miraeassetmobile.backend.domain.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import miraeassetmobile.backend.domain.entity.UserInfo;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    private String phoneNum;

    private String userName;

    private String userRole;


    private Long profileImgId;


    public UserInfo toUser(String phoneNum, String userName, String userRole, Long profileImgId){

        return UserInfo.builder()
                .userName(userName)
                .userRole(userRole)
                .phoneNum(phoneNum)
                .profileImgId(profileImgId)
                .build();
    }


}
