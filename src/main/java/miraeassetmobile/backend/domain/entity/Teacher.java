package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name="teacher")
@Getter
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private int id;
    @NotNull
    @Column(name = "teacher_name", length = 10)
    private String name;


    @NotNull
    @Column(name="phone_number",length = 10)
    private String phoneNum;


    @NotNull
    @Column(name="create_timestamp")
    private Timestamp createTimestamp;

    @NotNull
    @Column(name="modify_timestamp")
    private Timestamp modifyTimestamp;


    @Builder
    public Teacher(String name, String phoneNum){
        this.name=name;
        this.phoneNum=phoneNum;
    }


}
