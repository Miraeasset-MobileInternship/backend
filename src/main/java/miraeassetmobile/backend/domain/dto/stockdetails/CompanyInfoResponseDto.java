package miraeassetmobile.backend.domain.dto.stockdetails;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CompanyInfoResponseDto {


    String address;
    String country;
    String phoneNumber;
    String website;
    String industry;
    String sector;
    String employees;
    String ceo;

    String businessSummary;

}
