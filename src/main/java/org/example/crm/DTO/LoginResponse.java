package org.example.crm.DTO;

public class LoginResponse {
    private Long uid;
    private String token;

    public LoginResponse() {}

    public LoginResponse(Long uid, String token) {
        this.uid = uid;
        this.token = token;
    }

    // getter 和 setter
    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
