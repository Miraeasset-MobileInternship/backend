package miraeassetmobile.backend.domain.dto.transactions;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@Builder
public class TransferMoneyRequestDto {


    private Long studentId;
    private Long managerId;
    private Long categoryId;
    private String detail;
    private int money;


}
