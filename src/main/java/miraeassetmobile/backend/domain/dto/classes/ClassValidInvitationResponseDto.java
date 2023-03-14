package miraeassetmobile.backend.domain.dto.classes;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassValidInvitationResponseDto {

    Long classId;


    String title;


    String schoolName;


    int grade;


    int classNumber;

    String teacherName;



}
