package miraeassetmobile.backend.domain.enums;

public enum UserTypes {

    TEACHER("teacher"),
    STUDENT("student")

    ;

    private final String typeName;

    UserTypes(String name) {
        this.typeName = name;
    }

    public String getTypeName() {
        return typeName;
    }


}
