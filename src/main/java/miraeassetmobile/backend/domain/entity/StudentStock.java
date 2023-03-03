package miraeassetmobile.backend.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="student_stock")
@Getter
@NoArgsConstructor
public class StudentStock extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;

    @NotNull
    @Column(name = "student_id")
    private Long studentId;

    @NotNull
    @Column(name = "stock_isin_code")
    private String stockCode;

    @NotNull
    @Column(name = "blended_price")
    private int blendedPrice;

    @NotNull
    private int amount;


}
