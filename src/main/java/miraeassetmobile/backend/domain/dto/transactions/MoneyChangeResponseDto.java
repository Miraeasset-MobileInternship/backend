package miraeassetmobile.backend.domain.dto.transactions;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MoneyChangeResponseDto {


    boolean isPlus; //+인지 -인지

    int changeMoney;

    long lastDay;


}
