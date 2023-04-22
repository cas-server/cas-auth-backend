package com.cas.team.e.auth.controller;

import com.cas.team.e.auth.model.LoginUser;
import com.cas.team.e.auth.model.ResponseDTO;
import com.cas.team.e.auth.model.User;
import com.cas.team.e.auth.service.UserRegistrationService;
import com.cas.team.e.auth.utils.CasUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserRegistrationController {


    private final UserRegistrationService userRegistrationService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> loginUser(@RequestBody LoginUser loginUser) throws Exception {
        ResponseDTO responseDTO = userRegistrationService.getLoginUserTicket(loginUser);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
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
    public void deleteUserById(HttpServletRequest request, @PathVariable("Id") Integer Id) throws Exception {
        CasUtils casUtils = new CasUtils();
        String casTicket = request.getHeader("cas-ticket");
        boolean isValid = casUtils.validateCASTicket(casTicket);
        if (isValid) {
            System.out.println("valid ticket");
            this.userRegistrationService.deleteUser(Id);
        }
        else {
            throw new Exception("ticket is not valid");
        }
    }

    @GetMapping("/logout")
    public void deletCasTicket(HttpServletRequest request) throws Exception {
        CasUtils casUtils = new CasUtils();
        String casTicket = request.getHeader("cas-ticket");
        casUtils.deleteCASTicket(casTicket);
    }
}
