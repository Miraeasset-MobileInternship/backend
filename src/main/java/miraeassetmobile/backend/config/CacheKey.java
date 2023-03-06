package miraeassetmobile.backend.config;

public class CacheKey {
    private CacheKey() {}
    public static final int DEFAULT_EXPIRE_SEC = 60;
    public static final String TEACHER = "teacher";
    public static final String STUDENT = "student";
    public static final String TeacherRefreshToken = "TeacherRefreshToken";
    public static final int USER_EXPIRE_SEC = 120;
}
