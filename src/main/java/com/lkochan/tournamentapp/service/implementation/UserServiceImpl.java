package com.lkochan.tournamentapp.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lkochan.tournamentapp.entities.User;
import com.lkochan.tournamentapp.enums.UserRole;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.exception.EntityUtils;
import com.lkochan.tournamentapp.repository.UserRepository;
import com.lkochan.tournamentapp.service.interfaces.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        return EntityUtils.unwrapEntity(user, id, "user");
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("user", id);
        }
    }

    @Override
    public void updateUserCredentials(Long id, User userDetails) {
        User user = EntityUtils.unwrapEntity(userRepository.findById(id), id, "user");
        user.setUsername(userDetails.getUsername());
        user.setPassword(user.getPassword());
        userRepository.save(user);
    }

    @Override
    public void toggleBanUser(Long id) {
        User user = EntityUtils.unwrapEntity(userRepository.findById(id), id, "user");
        user.setIsBanned(!user.getIsBanned());
        userRepository.save(user);
    }

    @Override
    public void updateUserRole(Long id, UserRole newRole) {
        User user = EntityUtils.unwrapEntity(userRepository.findById(id), id, "user");
        if (user.getRole() != newRole) {
            user.setRole(newRole);
            userRepository.save(user);
        }
    }

}
