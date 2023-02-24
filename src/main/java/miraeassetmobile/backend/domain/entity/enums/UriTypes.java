package miraeassetmobile.backend.domain.entity.enums;

public enum UriTypes {

    TRANSACTION("transaction"),
    STUDENT("student"),
    JOB("class");

    private final String typeName;

    UriTypes(String name) {
        this.typeName = name;
    }

    public String getTypeName() {
        return typeName;
    }


}
