package miraeassetmobile.backend.domain.dto.jobs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import miraeassetmobile.backend.domain.entity.Job;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobCreateRequestDto {


    private Long classId;

    private String jobTitle; //직업명

    private String detail;

    private int monthlySalary;

    //is로 이름지으면 안된다 .getWithdrawStudent가 아니라 .isWithdrawStudent로 값이 가져와지기 때문에 이름이 중복임
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