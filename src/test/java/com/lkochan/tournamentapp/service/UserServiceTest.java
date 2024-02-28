package com.lkochan.tournamentapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lkochan.tournamentapp.entities.User;
import com.lkochan.tournamentapp.enums.UserRole;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.repository.UserRepository;
import com.lkochan.tournamentapp.service.implementation.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    private UserServiceImpl userService;
    private User user;
    private Long goodID, wrongID = 12345L;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, null);
        user = new User(1L, "Masuj__", "Jonny!", "matsuj1@o2.pl");
        goodID = user.getId();
    }

    @Test
    public void getUsersTest() {
        userService.getUsers();
        verify(userRepository).findAll();
    }

    @Test
    public void getUserByIdTest() {
        when(userRepository.findById(goodID)).thenReturn(Optional.of(user));

        User result = userService.getUser(goodID);
        
        assertEquals(user, result);
        verify(userRepository).findById(goodID);
    }

    @Test
    public void getUserByUsernameTest() {
        String username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getUser(username);
        
        assertEquals(user, result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void getUserNegativeTest() {
        when(userRepository.findById(wrongID)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            userService.getUser(wrongID);
        });

        
        assertTrue(thrown.getMessage().contains("user"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongID)));
    }

    @Disabled
    @Test
    public void saveUserTest() {
        userService.saveUser(user);
        verify(userRepository).save(user);
    }

    @Test
    public void updateUserCredentialsTest() {
        String updatedUsername = "Yozee", updatedPassword = "Cookie123";
        User updatedUserCredentials = new User(goodID, updatedUsername, updatedPassword, user.getEmail());

        when(userRepository.findById(goodID)).thenReturn(Optional.of(user));

        userService.updateUserCredentials(goodID, updatedUserCredentials);

        assertEquals(updatedUsername, userService.getUser(goodID).getUsername());
        assertEquals(updatedPassword, userService.getUser(goodID).getPassword());
        verify(userRepository).save(user);
    }

    @Test
    public void updateUserCredentialsNegativeTest() {
        String updatedUsername = "Yozee", updatedPassword = "Cookie123";
        User updatedUserCredentials = new User(goodID, updatedUsername, updatedPassword, user.getEmail());
        when(userRepository.findById(wrongID)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class,
            () -> userService.updateUserCredentials(wrongID, updatedUserCredentials)
        );

        assertTrue(thrown.getMessage().contains("user"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongID)));
    }

    @Test
    public void toggleBanUserTest() {
        boolean notBanned = user.getIsBanned();
        when(userRepository.findById(goodID)).thenReturn(Optional.of(user));

        userService.toggleBanUser(goodID);

        assertEquals(!notBanned, userService.getUser(goodID).getIsBanned());
        verify(userRepository).save(user);
    }

    @Test
    public void updateUserRoleTest() {
        UserRole admin = UserRole.ADMIN;
        when(userRepository.findById(goodID)).thenReturn(Optional.of(user));

        userService.updateUserRole(goodID, admin);

        assertEquals(admin, userService.getUser(goodID).getRole());
        verify(userRepository).save(user);
    }

    @Test
    public void deleteUserTest() {
        when(userRepository.findById(goodID)).thenReturn(Optional.of(user));
        userService.deleteUser(goodID);
        verify(userRepository).deleteById(goodID);
    }

    @Test
    public void deleteUserNegativeTest() {
        when(userRepository.findById(wrongID)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class,
            () -> userService.deleteUser(wrongID)
        );

        assertTrue(thrown.getMessage().contains("user"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongID)));
    }

}
