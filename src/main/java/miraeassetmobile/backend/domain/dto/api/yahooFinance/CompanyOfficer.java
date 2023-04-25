package miraeassetmobile.backend.domain.dto.api.yahooFinance;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class CompanyOfficer {

    int maxAge;

    String name ="-";
    int age;
    String title;
    int yearBorn;
    int fiscalYear;


    Values totalPay;
    Values exercisedValue;
    Values unexercisedValue;


}
