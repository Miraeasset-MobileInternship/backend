package miraeassetmobile.backend.domain.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInRequestDto {

    private String phoneNumber;


}
