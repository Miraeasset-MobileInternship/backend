package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="stock_batch")
@Getter
@NoArgsConstructor
public class StockBatch extends BaseTimeEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;



    @NotNull
    @Column(name = "stock_symbol")
    private String stockSymbol;


    @NotNull
    private String title;



    @Column(name = "regular_market_price")
    private double regularMarketPrice;



    @Column(name = "market_status")
    private String marketStatus;

    @Column(name = "type_display")
    private String typeDisplay;

    @Column(name = "full_exchange_name")
    private String fullExchangeName;

    @Column(name = "custom_price_alert_confidence")
    private String customPriceAlertConfidence;

    @Column(name = "regular_market_change")
    private double regularMarketChange;

    @Column(name = "regular_market_change_percent")
    private double regularMarketChangePercent;


    @Builder
    public StockBatch(String stockSymbol, String title, double regularMarketPrice, String marketStatus, String typeDisplay, String fullExchangeName, String customPriceAlertConfidence, double regularMarketChange, double regularMarketChangePercent) {
        this.stockSymbol = stockSymbol;
        this.title = title;
        this.regularMarketPrice = regularMarketPrice;
        this.marketStatus = marketStatus;
        this.typeDisplay = typeDisplay;
        this.fullExchangeName = fullExchangeName;
        this.customPriceAlertConfidence = customPriceAlertConfidence;
        this.regularMarketChange = regularMarketChange;
        this.regularMarketChangePercent = regularMarketChangePercent;
    }



    public StockBatch updateInfo (double regularMarketPrice, String marketStatus, String typeDisplay, String fullExchangeName, String customPriceAlertConfidence, double regularMarketChange, double regularMarketChangePercent) {
        this.regularMarketPrice = regularMarketPrice;
        this.marketStatus = marketStatus;
        this.typeDisplay = typeDisplay;
        this.fullExchangeName = fullExchangeName;
        this.customPriceAlertConfidence = customPriceAlertConfidence;
        this.regularMarketChange = regularMarketChange;
        this.regularMarketChangePercent = regularMarketChangePercent;

        return this;
    }


}
