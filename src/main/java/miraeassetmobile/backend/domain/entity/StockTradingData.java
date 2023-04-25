package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="stock_trading_data")
@Getter
@NoArgsConstructor
public class StockTradingData extends BaseTimeEntity{

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
    private int amount;

    @NotNull
    private int price;

    @NotNull
    @Column(name = "is_buying")
    private boolean buying;



    @Builder
    public StockTradingData(Long studentId, String stockSymbol, int amount, int price, boolean buying){
        this.studentId = studentId;
        this.stockSymbol = stockSymbol;
        this.amount = amount;
        this.price = price;
        this.buying = buying;
    }





}
