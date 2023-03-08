package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor
public class UserInfo extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;
    @NotNull
    @Column(name = "name", length = 10)
    private String name;


    @NotNull
    @Column(name="phone_number",length = 10)
    private String phoneNum;



    @NotNull
    private String role;


    @Builder
    public UserInfo(String name, String phoneNum, String role){
        this.name=name;
        this.phoneNum=phoneNum;
        this.role=role;
    }


}
