package miraeassetmobile.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="transaction_data")
@Getter
@NoArgsConstructor
public class TransactionData extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;
    @NotNull
    @Column(name = "transaction_money")
    private int money;


    @NotNull
    @Column(name = "manager_id")
    private Long managerId;

    @NotNull
    @Column(name = "student_id")
    private Long studentId;

    @NotNull
    @Column(name = "manager_job_id")
    private Long managerJobId;

    @NotNull
    @Column(name = "student_job_id")
    private Long studentJobId;


    @NotNull
    @Column(name = "category_id")
    private Long categoryId;

    @NotNull
    @Column(name = "class_id")
    private Long classId;

    @NotNull
    private String detail;

    @NotNull
    @Column(name = "from_who")
    private String from;


    @Builder
    public TransactionData(int money, Long managerId, Long managerJobId, Long studentId, Long studentJobId, Long classId, Long categoryId, String detail, String from) {
        this.money=money;
        this.managerId=managerId;
        this.managerJobId=managerJobId;
        this.studentId=studentId;
        this.studentJobId = studentJobId;
        this.classId=classId;
        this.categoryId=categoryId;
        this.detail=detail;
        this.from=from;
    }
}
