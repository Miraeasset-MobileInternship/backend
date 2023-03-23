package miraeassetmobile.backend.domain.dto.jobs;

import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.entity.Job;

@Getter
@Builder
public class JobCreateRequestDto {


    private Long classId;

    private String jobTitle; //직업명

    private String detail;

    private int monthlySalary;


    private boolean withdrawStudent;

    private boolean withdrawClass;



    public Job toJob(Long classId, String jobTitle, String detail, int monthlySalary, boolean withdrawStudent, boolean withdrawClass){
        return Job.builder()
                .classId(classId)
                .title(jobTitle)
                .monthlySalary(monthlySalary)
                .detail(detail)
                .isWithdrawStudent(withdrawStudent)
                .isWithdrawClass(withdrawClass)
                .build();
    }



}
