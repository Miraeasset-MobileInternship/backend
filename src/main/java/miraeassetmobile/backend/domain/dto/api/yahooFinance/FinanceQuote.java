package miraeassetmobile.backend.domain.dto.api.yahooFinance;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class FinanceQuote {


            double ask ;
            int askSize;

            Long averageDailyVolume10Day;

            Long averageDailyVolume3Month;

            double bid;

            int bidSize;

            double bookValue;

            String currency;

            String displayName;


            Long dividendDate;

            Long earningsTimestamp;

            Long earningsTimestampEnd;

            Long earningsTimestampStart;

            double epsCurrentYear;

            double epsForward;

            double epsTrailingTwelveMonths;

            boolean esgPopulated;

            String exchange;

            double exchangeDataDelayedBy;

            String exchangeTimezoneName;

            String exchangeTimezoneShortName;

            double fiftyDayAverage;
            double fiftyDayAverageChange;
            double fiftyDayAverageChangePercent;
            double fiftyTwoWeekHigh;
            double fiftyTwoWeekHighChange;
            double fiftyTwoWeekHighChangePercent;
            double fiftyTwoWeekLow;
            double fiftyTwoWeekLowChange;
            double fiftyTwoWeekLowChangePercent;

            String fiftyTwoWeekRange;
            String financialCurrency;
            Long firstTradeDateMilliseconds;
            double forwardPE;
            String fullExchangeName;
            Long gmtOffSetMilliseconds;
            String language;
            String longName;
            String market;
            Long marketCap;
            String marketState;
            String messageBoardId;
            double postMarketChange;
            double postMarketChangePercent;
            double postMarketPrice;
            Long postMarketTime;
            double priceEpsCurrentYear;
            int priceHint;
            double priceToBook;
            String quoteSourceName;
            String quoteType;
            String region;
            double regularMarketChange;
            double regularMarketChangePercent;
            double regularMarketDayHigh;
            double regularMarketDayLow;
            String regularMarketDayRange;
            double regularMarketOpen;
            double regularMarketPreviousClose;
            double regularMarketPrice;
            Long regularMarketTime;
            Long regularMarketVolume;
            Long sharesOutstanding;
            String shortName;
            int sourceInterval;
            String symbol;
            boolean tradeable;
            double trailingAnnualDividendRate;
            double trailingAnnualDividendYield;
            double trailingPE;
            boolean triggerable;
            double twoHundredDayAverage;
            double twoHundredDayAverageChange;
            double twoHundredDayAverageChangePercent;


}
