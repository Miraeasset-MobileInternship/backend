package miraeassetmobile.backend.domain.dto.classes;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassInvitationCodeResponseDto {

    Long classId;

    String invitationCode;


}
