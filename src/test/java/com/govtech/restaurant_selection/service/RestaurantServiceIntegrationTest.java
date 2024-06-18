package com.govtech.restaurant_selection.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

import com.govtech.restaurant_selection.dto.RestaurantDTO;
import com.govtech.restaurant_selection.modles.Restaurant;
import com.govtech.restaurant_selection.modles.RestaurantSelectionSession;
import com.govtech.restaurant_selection.modles.UserEntity;
import com.govtech.restaurant_selection.repository.RestaurantRepository;
import com.govtech.restaurant_selection.repository.SessionRepository;
import com.govtech.restaurant_selection.repository.UserRepository;
import com.govtech.restaurant_selection.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@DirtiesContext
public class RestaurantServiceIntegrationTest {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void clearDatabase() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        restaurantRepository.deleteAll();
    }

    @Test
    void proposeRestaurant() {

        UUID sessionId = saveSession(true);

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser");
            restaurantService.proposeARestaurant(sessionId, RestaurantDTO.builder().name("Test Restaurant 1").build());
            RestaurantSelectionSession session = sessionRepository.findById(sessionId).get();

            assertEquals(2, session.getRestaurants().size());
            assertEquals("Test Restaurant", session.getRestaurants().get(0).getRestaurantName());
        }

    }

    @Test
    void proposeRestaurantToInactiveSession() {

        UUID sessionId = saveSession(false);

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser");
            restaurantService.proposeARestaurant(sessionId, RestaurantDTO.builder().name("Test Restaurant 1").build());
            RestaurantSelectionSession session = sessionRepository.findById(sessionId).get();

            assertEquals(1, session.getRestaurants().size());
        }

    }

    private UUID saveSession(boolean isActive) {
        UserEntity user = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .username("testUser")
                .password("password")
                .build();
        RestaurantSelectionSession session = RestaurantSelectionSession.builder()
                .name("Test Session")
                .active(isActive)
                .owner(user)
                .build();
        session.setRestaurants(List.of(Restaurant.builder().restaurantName("Test Restaurant").user(user).restaurantSelectionSession(session).build()));
        session = sessionRepository.save(session);
        return session.getId();
    }

}
