package miraeassetmobile.backend.domain.dto.api.yahooFinance;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class FinanceQuote {


    double ask ;
    int askSize;

    Long averageDailyVolume10Day;

    Long averageDailyVolume3Month;
    String averageAnalystRating;

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
    String typeDisp;
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
    boolean cryptoTradeable;

    String customPriceAlertConfidence;
    Long circulatingSupply;
    String lastMarket;
    Long volume24Hr;

    Long volumeAllCurrencies;
    String fromCurrency;
    String toCurrency;
    String coinMarketCapLink;
    Long startDate;
    String coinImageUrl;
    String logoUrl;
    String ipoExpectedDate;
    double preMarketChangePercent;
    Long preMarketTime;

    double preMarketChange;

    double preMarketDayHigh;
    double preMarketDayLow;
    String preMarketDayRange;
    double preMarketOpen;
    double preMarketPreviousClose;
    double preMarketPrice;

    Long preMarketVolume;

    String prevName;
    String nameChangeDate;

}

