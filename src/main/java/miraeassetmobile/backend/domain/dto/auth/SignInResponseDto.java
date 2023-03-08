package miraeassetmobile.backend.domain.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInResponseDto {


    AccessTokenInfo tokenInfo;

    UserOnboardInfo userInfo;


}
