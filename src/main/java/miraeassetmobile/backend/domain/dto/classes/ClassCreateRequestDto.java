package miraeassetmobile.backend.domain.dto.classes;


import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.entity.Classes;

@Getter
@Builder
public class ClassCreateRequestDto {

    private Long teacher_id;

    private String title;

    private int grade;

    private int classNumber;

    private String currency;

    private String schoolName;


    public Classes toClass(Long teacher_id, String title, int grade, int classNumber, String currency, String schoolName){


        return Classes.builder()
                .teacherId(teacher_id)
                .classNum(classNumber)
                .schoolName(schoolName)
                .title(title)
                .grade(grade)
                .classNum(classNumber)
                .currency(currency)
                .build();

    }



}
