package com.govtech.restaurant_selection.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.govtech.restaurant_selection.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    void loginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void userRegistrationPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void registerNewUserShouldReturnErrorsWhenRequestIsMissing() throws Exception {

        mockMvc.perform(post("/register/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(4));
    }

    @Test
    void registerNewUserShouldReturnErrorWhenUsernameExists() throws Exception {
        Mockito.when(userService.isUserExists("testUser")).thenReturn(true);

        mockMvc.perform(
                        post("/register/user")
                                .param("username", "testUser")
                                .param("firstName", "Test")
                                .param("lastName", "User")
                                .param("password", "password")
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("register"))
                        .andExpect(model().attributeExists("user"))
                        .andExpect(model().hasErrors())
                        .andExpect(model().errorCount(1));
    }

    @Test
    void registerNewUserShouldSuccessfulWhenRequestIsValid() throws Exception {
        Mockito.when(userService.isUserExists("testUser")).thenReturn(false);

        mockMvc.perform(
                        post("/register/user")
                                .param("username", "testUser")
                                .param("firstName", "Test")
                                .param("lastName", "User")
                                .param("password", "password")
                )
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/login?success"))
                .andExpect(model().hasNoErrors());
    }

}
