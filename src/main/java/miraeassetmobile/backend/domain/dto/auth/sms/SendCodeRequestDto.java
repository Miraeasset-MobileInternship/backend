package miraeassetmobile.backend.domain.dto.auth.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendCodeRequestDto {

    String phoneNumber;
}
