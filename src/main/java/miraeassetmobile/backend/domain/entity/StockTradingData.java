package miraeassetmobile.backend.domain.entity;

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
    @Column(name = "stock_isin_code")
    private Long stockCode;


    @NotNull
    private int amount;

    @NotNull
    private int price;

    @NotNull
    @Column(name = "is_buying")
    private boolean isBuying;




}
