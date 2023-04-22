package com.cas.team.e.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class LoginUser {
    @Id
    private String username;
    private String password;
    private String permission;

    public LoginUser() {}

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginUser(String username, String password, String permission) {
        this.username = username;
        this.password = password;
        this.permission = permission;
    }
}
