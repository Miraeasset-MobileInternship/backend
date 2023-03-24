package miraeassetmobile.backend.domain.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="trending_every")
@Getter
@NoArgsConstructor
public class TrendingEvery extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;



    @Column(name = "data_string")
    private String dataString;



    @Builder
    TrendingEvery(String dataString){
        this.dataString = dataString;
    }

}
