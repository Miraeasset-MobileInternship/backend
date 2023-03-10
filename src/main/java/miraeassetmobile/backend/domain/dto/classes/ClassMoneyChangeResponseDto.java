package miraeassetmobile.backend.domain.dto.classes;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassMoneyChangeResponseDto {
    boolean isPlus; //+인지 -인지

    int changeMoney;

    long lastDay;

}
