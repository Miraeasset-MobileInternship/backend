package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="job")
@Getter
@NoArgsConstructor
public class Job extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;
    @NotNull
    @Column(name = "class_id")
    private Long classId;


    @NotNull
    @Column(length = 15)
    private String title; //직업명

    @NotNull
    @Column(name="monthly_salary")
    private int monthlySalary = 0;



    private String detail;

    @NotNull
    @Column(name="credit_limitation")
    private int creditLimit = 0;


    @NotNull
    @Column(name="is_withdraw_student")
    private boolean isWithdrawStudent;


    @NotNull
    @Column(name="is_withdraw_class")
    private boolean isWithdrawClass;


    @NotNull
    @Column(name="is_modify_credit")
    private boolean isModifyCredit = false;




    @Builder
    public Job(Long classId, String title, int monthlySalary, String detail, boolean isWithdrawStudent, boolean isWithdrawClass){
        this.classId= classId;
        this.title=title;
        this.monthlySalary=monthlySalary;
        this.detail=detail;
        this.isWithdrawStudent = isWithdrawStudent;
        this.isWithdrawClass = isWithdrawClass;
    }




}
