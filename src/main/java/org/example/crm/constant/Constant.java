package org.example.crm.constant;

public class Constant {
    // JWT 密钥和过期时间（秒），注意生产环境中应配置更安全的方式存放密钥
    public static final String SECRET = "MyJWTSecretKey";
    public static final long EXPIRATION = 36000L; // 10小时有效
    // 约定前缀，如 "Bearer "，前端请求头格式："Authorization: Bearer <token>"
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
}
