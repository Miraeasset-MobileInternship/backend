package miraeassetmobile.backend.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="class_stock")
@Getter
@NoArgsConstructor
public class ClassStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;

    @NotNull
    @Column(name = "class_id")
    private Long classId;

    @NotNull
    @Column(name = "isin_code")
    private String code;

    @NotNull
    private String title;

}
