package miraeassetmobile.backend.domain.enums;

public enum UriTypes {

//    USER("user"),

    TEACHER("teacher"),
    TRANSACTION("transaction"),
    STUDENT("student"),
    JOB("job"),

    USER("user"),

    CLASS("class");

    private final String typeName;

    UriTypes(String name) {
        this.typeName = name;
    }

    public String getTypeName() {
        return typeName;
    }


}
