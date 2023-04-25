package miraeassetmobile.backend.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="trending_stocks")
@Getter
@NoArgsConstructor
public class TrendingStocks extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA 사용시 필요)
    private Long id;

    String symbol;

    String title;

    String price;

    @Column(name = "change_price")
    String changePrice;

    @Column(name = "change_percent")
    String changePercent;

    @Column(name = "change_status")
    int changeStatus;


    @Column(name = "tag_type")
    String tagType;

    @Column(name = "tag_market")
    String tagMarket;

    @Column(name = "tag_confidence")
    String tagConfidence;

    @Column(name = "is_open")
    boolean open;

    @Builder
    public TrendingStocks(String symbol, String title, String price, String changePrice, String changePercent, int changeStatus, String tagType, String tagMarket, String tagConfidence, boolean open){
        this.symbol=symbol;
        this.title=title;
        this.price = price;
        this.changePrice = changePrice;
        this.changePercent=changePercent;
        this.changeStatus=changeStatus;
        this.tagType = tagType;
        this.tagMarket = tagMarket;
        this.tagConfidence = tagConfidence;
        this.open=open;
    }

    public TrendingStocks updateInfo(String price, String changePrice, String changePercent, int changeStatus, String tagType, String tagMarket, String tagConfidence, boolean open) {
        this.price = price;
        this.changePrice = changePrice;
        this.changePercent = changePercent;
        this.changeStatus = changeStatus;
        this.tagType = tagType;
        this.tagMarket = tagMarket;
        this.tagConfidence = tagConfidence;
        this.open = open;

        return this;
    }


}
