package miraeassetmobile.backend.domain.dto.transactions;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransactionCategoryDto {

    Long categoryId;

    String categoryTitle;


}
