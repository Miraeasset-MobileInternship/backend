package miraeassetmobile.backend.domain.entity;

//entity: DB와 직접적으로 연결됨

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;


@Entity
@Table(name="teacher")
@Getter
@NoArgsConstructor
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private int id;
    @NotNull
    @Column(name = "teacher_id")
    private int teacherId;


    @NotNull
    @Column(length = 10)
    private String title;

    @NotNull
    @Column(length=2)
    private int grade;

    @NotNull
    @Column(name="class_number", length = 2)
    private int classNum;

    @NotNull
    @Column(length = 5)
    private String currency;

    @NotNull
    private int money;

    @NotNull
    @Column(name="create_timestamp")
    private Timestamp createTimestamp;

    @NotNull
    @Column(name="modify_timestamp")
    private Timestamp modifyTimestamp;



    // default 제외 나머지
    @Builder
    public Class(int teacherId, String title, int grade, int classNum, String currency){
        this.teacherId = teacherId;
        this.title = title;
        this.grade=grade;
        this.classNum=classNum;
        this.currency=currency;
    }


}
