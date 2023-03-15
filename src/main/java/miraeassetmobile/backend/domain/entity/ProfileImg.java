package miraeassetmobile.backend.domain.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="profile_img")
@Getter
@NoArgsConstructor
public class ProfileImg {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;



    @NotNull
    @Column(name = "icon_code")
    private String iconCode;


}
