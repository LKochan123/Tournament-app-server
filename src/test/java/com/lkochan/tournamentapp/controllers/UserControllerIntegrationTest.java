package com.lkochan.tournamentapp.controllers;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkochan.tournamentapp.entities.User;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.service.interfaces.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc()
public class UserControllerIntegrationTest {
    
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UserService userService;
    private List<User> users;
    private EntityNotFoundException exception;
    private Long id, wrongID = 12345L;

    @BeforeEach
    public void setUp() {
        users = createUsers();
        users.forEach(user -> userService.saveUser(user));
        exception = new EntityNotFoundException("user", wrongID);
        id = users.get(0).getId();
    }

    @AfterEach
    public void tearDown() {
        userService.getUsers().clear();
    }

    @Disabled
    @Test
    public void getUsersTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/all");

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Disabled
    @Test
    public void getUserTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/{id}", id);
        when(userService.getUser(id)).thenReturn(users.get(0));

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("Yolo123"))
            .andExpect(jsonPath("$.email").value("masuj@o2.pl"));
    }

    @Disabled
    @Test
    public void getInvalidUserTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/{id}", wrongID);
        when(userService.getUser(wrongID)).thenThrow(exception);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Disabled
    @Test
    public void saveValidUserTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new User("CookMe", "cookMe", "cook@o2.pl")));

        mockMvc.perform(request).andExpect(status().isCreated());
    }

    @Disabled
    @Test
    public void saveInvalidUserTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new User("Elz", "  ", "elz@o2.pl")));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Disabled
    @Test
    public void deleteUserTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/user/{id}", id);
        doNothing().when(userService).deleteUser(id);
        mockMvc.perform(request).andExpect(status().isNoContent());
    }

    @Disabled
    @Test
    public void deleteInvalidUserTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/user/{id}", wrongID);
        doThrow(exception).when(userService).deleteUser(wrongID);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    private List<User> createUsers() {
        return Arrays.asList(
            new User(1L, "Masuj__", "Yolo123", "masuj@o2.pl"),
            new User(2L, "Yozee", "Cookie123", "yozee@tlen.pl")
        );
    }

}
