package com.cas.team.e.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class LoginUser {
    @Id
    private String username;
    private String password;

    public LoginUser() {}

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
