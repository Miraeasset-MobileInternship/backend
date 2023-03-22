package miraeassetmobile.backend.domain.enums;

public enum TransactionFromTypes {

    //입출금
    STUDENT("student"),
    CLASS("class"),

    //매도매수
    SELL("sell"),
    BUY("buy")

    ;

    private final String typeName;

    TransactionFromTypes(String name) {
        this.typeName = name;
    }

    public String getTypeName() {
        return typeName;
    }

}
