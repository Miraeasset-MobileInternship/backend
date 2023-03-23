package miraeassetmobile.backend.domain.dto.api.yahooFinance;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Getter
@Component
@NoArgsConstructor

public class FinanceQuote {

    @Nullable
    double ask ;
    @Nullable
    int askSize;
    @Nullable
    Long averageDailyVolume10Day;
    @Nullable
    Long averageDailyVolume3Month;
    @Nullable
    String averageAnalystRating;
    @Nullable
    double bid;
    @Nullable
    int bidSize;
    @Nullable
    double bookValue;
    @Nullable
    String currency;
    @Nullable
    String displayName;

    @Nullable
    Long dividendDate;
    @Nullable
    String exchangeTransferDate;
    @Nullable
    Long earningsTimestamp;
    @Nullable
    Long earningsTimestampEnd;
    @Nullable
    Long earningsTimestampStart;
    @Nullable
    double epsCurrentYear;
    @Nullable
    double epsForward;
    @Nullable
    double epsTrailingTwelveMonths;
    @Nullable
    boolean esgPopulated;
    @Nullable
    String exchange;
    @Nullable
    double exchangeDataDelayedBy;
    @Nullable
    String exchangeTimezoneName;
    @Nullable
    String exchangeTimezoneShortName;
    @Nullable
    double fiftyDayAverage;
    @Nullable
    double fiftyDayAverageChange;
    @Nullable
    double fiftyDayAverageChangePercent;
    @Nullable
    double fiftyTwoWeekHigh;
    @Nullable
    double fiftyTwoWeekHighChange;
    @Nullable
    double fiftyTwoWeekHighChangePercent;
    @Nullable
    double fiftyTwoWeekLow;
    @Nullable
    double fiftyTwoWeekLowChange;
    @Nullable
    double fiftyTwoWeekLowChangePercent;
    @Nullable
    String fiftyTwoWeekRange;
    @Nullable
    String financialCurrency;
    @Nullable
    Long firstTradeDateMilliseconds;
    @Nullable
    double forwardPE;
    @Nullable
    String fullExchangeName;
    @Nullable
    Long gmtOffSetMilliseconds;
    @Nullable
    String language;
    @Nullable
    String longName;
    @Nullable
    String market;
    @Nullable
    Long marketCap;
    @Nullable
    String marketState;
    @Nullable
    String messageBoardId;
    @Nullable
    double postMarketChange;
    @Nullable
    double postMarketChangePercent;
    @Nullable
    double postMarketPrice;
    @Nullable
    Long postMarketTime;
    @Nullable
    double priceEpsCurrentYear;
    @Nullable
    int priceHint;
    @Nullable
    double priceToBook;
    @Nullable
    String quoteSourceName;
    @Nullable
    String quoteType;
    @Nullable
    String typeDisp;
    @Nullable
    String region;
    @Nullable
    double regularMarketChange;
    @Nullable
    double regularMarketChangePercent;
    @Nullable
    double regularMarketDayHigh;
    @Nullable
    double regularMarketDayLow;
    @Nullable
    String regularMarketDayRange;
    @Nullable
    double regularMarketOpen;
    @Nullable
    double regularMarketPreviousClose;
    @Nullable
    double regularMarketPrice;
    @Nullable
    Long regularMarketTime;
    @Nullable
    Long regularMarketVolume;
    @Nullable
    Long sharesOutstanding;
    @Nullable
    String shortName;
    @Nullable
    int sourceInterval;
    @Nullable
    String symbol;
    @Nullable
    boolean tradeable;
    @Nullable
    double trailingAnnualDividendRate;
    @Nullable
    double trailingAnnualDividendYield;
    @Nullable
    double trailingPE;
    @Nullable
    boolean triggerable;
    @Nullable
    double twoHundredDayAverage;
    @Nullable
    double twoHundredDayAverageChange;
    @Nullable
    double twoHundredDayAverageChangePercent;
    @Nullable
    boolean cryptoTradeable;
    @Nullable
    String customPriceAlertConfidence;
    @Nullable
    Long circulatingSupply;
    @Nullable
    String lastMarket;
    @Nullable
    Long volume24Hr;
    @Nullable
    Long volumeAllCurrencies;
    @Nullable
    String fromCurrency;
    @Nullable
    String toCurrency;
    @Nullable
    String coinMarketCapLink;
    @Nullable
    Long startDate;
    @Nullable
    String coinImageUrl;
    @Nullable
    String logoUrl;
    @Nullable
    String ipoExpectedDate;
    @Nullable
    double preMarketChangePercent;
    @Nullable
    Long preMarketTime;
    @Nullable
    double preMarketChange;
    @Nullable
    double preMarketDayHigh;
    @Nullable
    double preMarketDayLow;
    @Nullable
    String preMarketDayRange;
    @Nullable
    double preMarketOpen;
    @Nullable
    double preMarketPreviousClose;
    @Nullable
    double preMarketPrice;

    @Nullable
    String expireIsoDate;
    @Nullable
    String newListingDate;
    @Nullable
    Long preMarketVolume;
    @Nullable
    Long openInterest;
    @Nullable
    String prevName;
    @Nullable
    String nameChangeDate;

    @Nullable
    String prevExchange;
    @Nullable
    double trailingThreeMonthNavReturns;
    @Nullable
    double ytdReturn;
    @Nullable
    double trailingThreeMonthReturns;

    @Nullable
    String underlyingExchangeSymbol;

    @Nullable
    String underlyingSymbol;


    @Nullable
    String headSymbolAsString;

    @Nullable
    boolean contractSymbol;

    @Nullable
    Long expireDate;

}

