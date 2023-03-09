package miraeassetmobile.backend.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassOnboardInfo {


    Long classId;

    String title;

    String grade;

    Long teacherId;


    String currency;


    Timestamp createTimestamp;

}
