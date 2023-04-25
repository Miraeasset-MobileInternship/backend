package miraeassetmobile.backend.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOnboardInfo {

    Long userId;


    String userRole;


    String userName;

    String profileImg;


    UserClassInfo classInfo;

}