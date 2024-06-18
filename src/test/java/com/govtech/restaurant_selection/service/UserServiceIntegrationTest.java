package com.govtech.restaurant_selection.service;

import static org.junit.jupiter.api.Assertions.*;

import com.govtech.restaurant_selection.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void saveNewUser(){
        UserDTO userDTO = UserDTO.builder()
                                .firstName("Test")
                                .lastName("User")
                                .username("testUser123")
                                .password("password")
                                .build();
        userService.saveUser(userDTO);
        assertTrue(userService.isUserExists("testUser123"));
    }

}
