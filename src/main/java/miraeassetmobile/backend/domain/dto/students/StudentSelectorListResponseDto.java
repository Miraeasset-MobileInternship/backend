package miraeassetmobile.backend.domain.dto.students;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


//v2에서는 사용 안함
@Getter
@Builder
public class StudentSelectorListResponseDto {

    List<StudentTransferSelectorDto> studentSelectorList;

}
