package miraeassetmobile.backend.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessTokenInfo {

    private String grantType;
    private String accessToken;

    private Long accessTokenExpiresIn;


}
