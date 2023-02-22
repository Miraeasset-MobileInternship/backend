package miraeassetmobile.backend.domain.dto.students;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentJobDto {

    //학생의 pk
    Long id;

    //학생의 번호
    int number;

    //학생의 이름
    String studentName;


    //직업의 id
    Long jobId;

    //직업명
    String jobTitle;



}
