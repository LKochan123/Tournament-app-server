package com.lkochan.tournamentapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lkochan.tournamentapp.entities.User;
import com.lkochan.tournamentapp.enums.UserRole;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.service.interfaces.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) throws EntityNotFoundException {
        User user = userService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) throws EntityNotFoundException {
        User user = userService.getUser(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> saveUser(@RequestBody @Valid User user) {
        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) throws EntityNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/credentials/{id}")
    public ResponseEntity<HttpStatus> updateUserCredentials(@RequestBody @Valid User user, @PathVariable Long id) throws EntityNotFoundException {
        userService.updateUserCredentials(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRole> updateUserRole(@RequestParam("role") UserRole role, @PathVariable Long id) throws EntityNotFoundException {
        userService.updateUserRole(id, role);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @PutMapping("/is-banned/{id}")
    public ResponseEntity<Boolean> toggleBanUser(@PathVariable Long id) throws EntityNotFoundException {
        boolean isCurrentlyBanned = !userService.getUser(id).getIsBanned();
        userService.toggleBanUser(id);
        return new ResponseEntity<>(isCurrentlyBanned, HttpStatus.OK);
    }
}
