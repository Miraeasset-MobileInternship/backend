package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name="student")
@Getter
@NoArgsConstructor
public class Student {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;
    @NotNull
    @Column(name = "class_id")
    private Long classId;

    @NotNull
    @Column(name = "job_id")
    private Long jobId;


    @NotNull
    @Column(name="student_name", length = 10)
    private String name; //이름

    @NotNull
    @Column(length=2)
    private int number;


    @NotNull
    private int money;


    @NotNull
    @Column(name="phone_number", length = 15)
    private String phoneNum;


    @NotNull
    @Column(name="credit_score")
    private int creditScore;


    @NotNull
    @Column(name="create_timestamp")
    private Timestamp createTimestamp;

    @NotNull
    @Column(name="modify_timestamp")
    private Timestamp modifyTimestamp;



    // default 제외 나머지
    @Builder
    public Student(String name, int number, int money, String phoneNum){
        this.name=name;
        this.number=number;
        this.money=money;
        this.phoneNum=phoneNum;
    }


}
