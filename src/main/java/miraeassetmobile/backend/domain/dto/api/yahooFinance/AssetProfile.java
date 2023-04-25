package miraeassetmobile.backend.domain.dto.api.yahooFinance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@NoArgsConstructor
public class AssetProfile {

    String address1 ="-";
    String city ="-";
    String state ="-";
    String zip ="-";
    String country ="-";
    String phone ="-";
    String website ="-";
    String industry ="-";
    String sector ="-";
    String longBusinessSummary ="-";
    String fullTimeEmployees ="-";

    List<CompanyOfficer> companyOfficers;

    int auditRisk;

    int boardRisk;

    int compensationRisk;

    int shareHolderRightsRisk;

    int overallRisk;

    Long governanceEpochDate;

    Long compensationAsOfEpochDate;

    Long maxAge;

    String fax ="-";

    String address2 = "-";

}
