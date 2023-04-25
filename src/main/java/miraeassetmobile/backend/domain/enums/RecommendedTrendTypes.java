package miraeassetmobile.backend.domain.enums;

public enum RecommendedTrendTypes {


    ////hsl(124, 70%, 50%) 형식
    STRONGBUY("Strong Buy","hsl(0, 100%, 50%)"),
    BUY("Buy","hsl(147, 50%, 47%)"),

    HOLD("Hold","hsl(39, 100%, 50%)"),
    SELL("Sell","hsl(300, 76%, 72%)"),
    STRONGSELL("Strong Sell","hsl(248, 53%, 58%)"),

    ;

    private final String typeName;
    private final String colorCode;


    RecommendedTrendTypes(String typeName, String colorCode) {
        this.typeName = typeName;
        this.colorCode = colorCode;
    }

    public String getTypeName() {
        return typeName;
    }
    public String getColorCode() {
        return colorCode;
    }

}
