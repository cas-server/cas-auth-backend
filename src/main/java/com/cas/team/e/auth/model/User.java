package com.cas.team.e.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Entity(name="Users")
public class User {
    @Id
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String permission;

    public User (String username, String email, String password, String permission) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.permission = permission;
    }

    public User() {

    }

    public void setId(Integer id) {
        this.id = id;
    }

    @javax.persistence.Id
    public Integer getId() {
        return id;
    }
}
