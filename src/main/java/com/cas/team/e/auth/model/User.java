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

    public User (String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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
