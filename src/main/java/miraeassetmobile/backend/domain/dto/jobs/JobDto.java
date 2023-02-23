package miraeassetmobile.backend.domain.dto.jobs;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@Builder
public class JobDto { //forntend로 전달용

    Long id;

    private String title; //직업명

    private String detail;

    private int monthlySalary;

    private int creditLimit;
    private boolean isWithdrawStudent;

    private boolean isWithdrawClass;

    private boolean isModifyCredit;


}
