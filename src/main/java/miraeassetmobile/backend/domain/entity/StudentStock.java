package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

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
    @Column(name = "stock_symbol")
    private String stockSymbol;

    @NotNull
    @Column(name = "blended_price")
    private BigDecimal blendedPrice;

    @NotNull
    private int amount;



    public StudentStock updateAmount(int amount){
        this.amount = amount;

        return this;
    }


    public StudentStock updateBlendedPrice(BigDecimal blendedPrice){
        this.blendedPrice = blendedPrice;

        return this;
    }

    @Builder
    public StudentStock(Long studentId, String stockSymbol, BigDecimal blendedPrice, int amount){
        this.studentId = studentId;
        this.stockSymbol = stockSymbol;
        this.blendedPrice = blendedPrice;
        this.amount = amount;
    }



}
