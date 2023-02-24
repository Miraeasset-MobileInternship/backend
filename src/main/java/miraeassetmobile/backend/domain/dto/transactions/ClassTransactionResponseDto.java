package miraeassetmobile.backend.domain.dto.transactions;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ClassTransactionResponseDto {

    int currentPage;

    int maxPage;

    int totalData;

    List<ClassTransactionDataDto> classTransactionData;

}
