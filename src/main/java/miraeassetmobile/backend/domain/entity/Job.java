package miraeassetmobile.backend.domain.entity;


//entity: DB와 직접적으로 연결됨

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;


@Entity
@Table(name="job")
@Getter
@NoArgsConstructor
public class Job extends BaseTimeEntity {

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
    private boolean isWithdrawStudent = false;


    @NotNull
    @Column(name="is_withdraw_class")
    private boolean isWithdrawClass = false;


    @NotNull
    @Column(name="is_modify_credit")
    private boolean isModifyCredit = false;


//    @Column(name="create_timestamp")
//    @CreatedDate
//    private Timestamp createTimestamp;
//
//
//    @Column(name="modify_timestamp")
//    @LastModifiedDate
//    private Timestamp modifyTimestamp;




    @Builder
    public Job(Long classId, String title, int monthlySalary, String detail, int creditLimit, boolean isWithdrawStudent, boolean isWithdrawClass, boolean isModifyCredit){
        this.classId= classId;
        this.title=title;
        this.monthlySalary=monthlySalary;
        this.detail=detail;
        this.creditLimit=creditLimit;
        this.isWithdrawStudent =isWithdrawStudent;
        this.isWithdrawClass=isWithdrawClass;
        this.isModifyCredit=isModifyCredit;
    }



}

