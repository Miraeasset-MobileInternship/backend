package miraeassetmobile.backend.domain.dto.jobs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentJobUpdateRequestDto {

    Long studentId;

    Long jobId;

}
