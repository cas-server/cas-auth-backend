package com.cas.team.e.auth.controller;

import com.cas.team.e.auth.model.LoginUser;
import com.cas.team.e.auth.model.User;
import com.cas.team.e.auth.service.UserRegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginUser loginUser) {
        LoginUser user = userRegistrationService.getLoginUserTicket(loginUser);
        return new ResponseEntity<>(user.getUsername(), HttpStatus.CREATED);
    }

    @PostMapping("/newUser")
    public ResponseEntity<User> registerUser(@RequestBody User user) throws Exception {
        User newUser = this.userRegistrationService.saveUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = this.userRegistrationService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{Id}")
    public void deleteUserById(@PathVariable("Id") Integer Id) {
        this.userRegistrationService.deleteUser(Id);
    }
}
