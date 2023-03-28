package miraeassetmobile.backend.domain.dto.auth.sms;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendCodeRequestDto {

    String phoneNumber;
}
