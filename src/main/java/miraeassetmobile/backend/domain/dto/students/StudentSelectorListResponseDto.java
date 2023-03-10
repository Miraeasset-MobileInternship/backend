package miraeassetmobile.backend.domain.dto.students;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentSelectorListResponseDto {

    List<StudentTransferSelectorDto> studentSelectorList;

}
