package miraeassetmobile.backend.domain.entity.enums;

public enum TransactionFromTypes {

    STUDENT("student"),
    CLASS("class");

    private final String typeName;

    TransactionFromTypes(String name) {
        this.typeName = name;
    }

    public String getTypeName() {
        return typeName;
    }

}
