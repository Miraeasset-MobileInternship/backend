package miraeassetmobile.backend.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassOnboardInfo {


    Long classId; //학급 아이디

    String title;//나라이름

    String grade; //학년, 반

    Long teacherId; //담당 선생님

    String currency; //화폐 단위


    Timestamp createTimestamp; //생성일자

}
