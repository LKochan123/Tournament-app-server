package com.lkochan.tournamentapp.service.interfaces;

import java.util.List;

import com.lkochan.tournamentapp.entities.User;
import com.lkochan.tournamentapp.enums.UserRole;

public interface UserService {
    List<User> getUsers();
    User getUser(Long id);
    void saveUser(User user);
    void deleteUser(Long id);
    void updateUserCredentials(Long id, User user);
    void toggleBanUser(Long id);
    void updateUserRole(Long id, UserRole newRole);
}
