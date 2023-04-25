package miraeassetmobile.backend.domain.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyRequestDto {


    private Long studentId;
    private Long managerId;
    private Long categoryId;
    private String detail;
    private int money;


}
