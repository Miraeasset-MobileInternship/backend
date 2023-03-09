package miraeassetmobile.backend.config;

import lombok.Getter;

@Getter
public class CacheKey {

    public static final int DEFAULT_EXPIRE_SEC = 60;
    public static final String USER = "user";
    public static final String RefreshToken = "RefreshToken";
    public static final String LogoutAccessToken = "LogoutAccessToken";
}
