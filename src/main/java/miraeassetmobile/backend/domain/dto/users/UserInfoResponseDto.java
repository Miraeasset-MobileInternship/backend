package miraeassetmobile.backend.domain.dto.users;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoResponseDto {

    private String userName;

    private String profileImg;

}
