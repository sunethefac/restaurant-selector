package com.govtech.restaurant_selection.controller;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.govtech.restaurant_selection.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@WebMvcTest(RestaurantController.class)
public class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;


    @Test
    void proposeRestaurantForSessionShouldReturnErrorsWhenRequestIsMissing() throws Exception {
        UUID sessionId = UUID.randomUUID();
        mockMvc.perform(
                        post("/restaurant/add/" + sessionId)
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                        )
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/sessions/" + sessionId + "?restaurantValidationError"));
    }

    @Test
    void proposeRestaurantForSessionShouldSuccessWhenRequestIsValid() throws Exception {

        UUID sessionId = UUID.randomUUID();

        mockMvc.perform(
                        post("/restaurant/add/" + sessionId)
                                        .contentType(APPLICATION_FORM_URLENCODED)
                                        .param("name", "Test Restaurant")
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                        )
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/sessions/" + sessionId + "?restaurantAdded"))
                        .andExpect(model().hasNoErrors());
    }




}
