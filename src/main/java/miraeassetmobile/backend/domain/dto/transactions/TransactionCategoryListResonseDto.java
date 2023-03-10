package miraeassetmobile.backend.domain.dto.transactions;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TransactionCategoryListResonseDto {

    List<TransactionCategoryDto> categoryList;

}
