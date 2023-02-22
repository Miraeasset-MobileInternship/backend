package miraeassetmobile.backend.domain.dto.jobs;

import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.entity.Job;

@Getter
@Builder
public class JobCreateDto {


    private Long classId;

    private String jobTitle; //직업명

    private String detail;

    private int monthlySalary;


    private boolean isWithdrawStudent;

    private boolean isWithdrawClass;



    public Job toJob(Long classId, String jobTitle, String detail, int monthlySalary, boolean isWithdrawStudent, boolean isWithdrawClass){
        return Job.builder()
                .classId(classId)
                .title(jobTitle)
                .monthlySalary(monthlySalary)
                .detail(detail)
                .isWithdrawClass(isWithdrawClass)
                .isWithdrawStudent(isWithdrawStudent)
                .build();
    }



}
