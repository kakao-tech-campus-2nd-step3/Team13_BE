package dbdr.global.util.api;

public class JwtUtils {
    public static final String ISSUER = "CareBridge";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 60 * 60 * 24 * 30L; // 30일
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60; // 1시간

    private JwtUtils() {
    }
}
