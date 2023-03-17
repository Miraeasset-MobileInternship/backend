package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="students")
@Getter
@NoArgsConstructor
public class Student extends BaseTimeEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;
    @NotNull
    @Column(name = "class_id")
    private Long classId;

    @NotNull
    @Column(name = "job_id")
    private Long jobId = 2L;

    @NotNull
    @Column(name = "user_id")
    private Long userId;



//    @NotNull
//    @Column(name="student_name", length = 10)
//    private String name; //이름

    @NotNull
    @Column(length=2)
    private int number;


    @NotNull
    private int money = 0;


//    @NotNull
//    @Column(name="phone_number", length = 15)
//    private String phoneNum;


    @NotNull
    @Column(name="credit_score")
    private int creditScore = 0;




    public Student updateJob(Long jobId){
        this.jobId = jobId;

        return this;
    }

    public Student updateMoney(int money){
        this.money = money;

        return this;
    }

    // default 제외 나머지
    @Builder
    public Student(Long userId, Long classId, int number){
//        this.name=name;
        this.userId = userId;
        this.number=number;
        this.classId = classId;
//        this.phoneNum=phoneNum;
    }




}
