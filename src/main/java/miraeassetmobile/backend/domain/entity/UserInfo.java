package miraeassetmobile.backend.domain.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="user_info")
@Getter
@NoArgsConstructor
public class UserInfo extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;
    @NotNull
    @Column(name = "user_name")
    private String userName;


    @NotNull
    @Column(name="phone_number")
    private String phoneNum;



    @NotNull
    @Column(name = "user_role")
    private String userRole;


    @Builder
    public UserInfo(String userName, String phoneNum, String userRole){
        this.userName=userName;
        this.phoneNum=phoneNum;
        this.userRole = userRole;
    }


}
