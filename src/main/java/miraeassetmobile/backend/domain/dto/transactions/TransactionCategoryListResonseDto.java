package miraeassetmobile.backend.domain.dto.transactions;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


//v2에서는 사용안함
@Builder
@Getter
public class TransactionCategoryListResonseDto {

    List<TransactionCategoryDto> categoryList;

}
