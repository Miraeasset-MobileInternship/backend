package miraeassetmobile.backend.domain.dto.jobs;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentJobUpdateRequestDto {

    Long studentId;

    Long jobId;

}
