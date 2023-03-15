package miraeassetmobile.backend.domain.dto.api.yahooFinance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrendingByRegion {


    int count;

    Long jobTimestamp;

    List<Symbol> quotes;


    Long startInterval;




}
