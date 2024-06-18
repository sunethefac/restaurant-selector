package com.govtech.restaurant_selection.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import com.govtech.restaurant_selection.dto.RestaurantSelectionSessionDTO;
import com.govtech.restaurant_selection.modles.Restaurant;
import com.govtech.restaurant_selection.modles.RestaurantSelectionSession;
import com.govtech.restaurant_selection.modles.UserEntity;
import com.govtech.restaurant_selection.repository.RestaurantRepository;
import com.govtech.restaurant_selection.repository.SessionRepository;
import com.govtech.restaurant_selection.repository.UserRepository;
import com.govtech.restaurant_selection.security.SecurityUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@DirtiesContext
public class SessionServiceIntegrationTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @AfterEach
    @Transactional
    void clearDatabase() {
        restaurantRepository.deleteAll();
        sessionRepository.deleteAll();
        userRepository.deleteAll();
    }

//    @AfterTestClass
//    void clearDB() {
//        sessionRepository.deleteAll();
//        userRepository.deleteAll();
//        restaurantRepository.deleteAll();
//    }

    @Test
    void testCreateSession(){

        RestaurantSelectionSessionDTO selectionSessionDTO = new RestaurantSelectionSessionDTO();
        selectionSessionDTO.setSessionName("Test Session");
        selectionSessionDTO.setRestaurantName("Restaurant 1");

        UserEntity userEntity = UserEntity.builder()
                                    .userId(1L)
                                    .firstName("Test")
                                    .lastName("User")
                                    .username("testUser")
                                    .password("password")
                                    .build();
        userRepository.save(userEntity);
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser");
            RestaurantSelectionSession session = sessionService.createSession(selectionSessionDTO);
            assertNotNull(session);
            assertNotNull(session.getId());
            assertEquals(1, session.getRestaurants().size());
            assertEquals("Restaurant 1", session.getRestaurants().get(0).getRestaurantName());
            assertEquals("Test" , session.getOwner().getFirstName());
        }
    }

    @Test
    void testGetActiveSession(){
        UUID sessionId = saveSessionData();

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser1");
            RestaurantSelectionSession session = sessionService.getSession(sessionId);

            assertNotNull(session);
            assertNotNull(session.getId());
            assertNull(session.getSelectedRestaurant());
            assertEquals(3, session.getRestaurants().size());
        }
    }

    @Test
    void testCloseSession(){
        UUID sessionId = saveSessionData();

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser1");
            RestaurantSelectionSession closedSession = sessionService.closeSession(sessionId);

            assertNotNull(closedSession);
            assertNotNull(closedSession.getId());
            assertNotNull(closedSession.getSelectedRestaurant());
            assertEquals(3, closedSession.getRestaurants().size());
            assertEquals(closedSession.getSelectedRestaurant().getRestaurantName(), closedSession.getRestaurants().get(0).getRestaurantName());
            assertTrue(closedSession.getRestaurants().get(0).isSelected());
        }
    }

    @Test
    void testGetInactiveSession(){
        UUID sessionId = saveSessionData();
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser1");
            sessionService.closeSession(sessionId);
            RestaurantSelectionSession closedSession = sessionService.getSession(sessionId);

            assertNotNull(closedSession);
            assertNotNull(closedSession.getId());
            assertNotNull(closedSession.getSelectedRestaurant());
            assertEquals(3, closedSession.getRestaurants().size());
            assertEquals(closedSession.getSelectedRestaurant().getRestaurantName(), closedSession.getRestaurants().get(0).getRestaurantName());
            assertTrue(closedSession.getRestaurants().get(0).isSelected());
        }
    }


    private UUID saveSessionData() {

        UserEntity user1 = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .username("testUser1")
                .password("password")
                .build();

        UserEntity user2 = UserEntity.builder()
                .firstName("Test1")
                .lastName("User")
                .username("testUser2")
                .password("password")
                .build();

        UserEntity user3 = UserEntity.builder()
                .firstName("Test2")
                .lastName("User")
                .username("testUser3")
                .password("password")
                .build();

        RestaurantSelectionSession session = RestaurantSelectionSession.builder()
                .name("Test Session")
                .active(true)
                .owner(user1)
                .build();

        List<Restaurant> restaurants = List.of(Restaurant.builder().restaurantName("Test Restaurant 1").user(user1).restaurantSelectionSession(session).build(),
                Restaurant.builder().restaurantName("Test Restaurant 2").user(user2).restaurantSelectionSession(session).build(),
                Restaurant.builder().restaurantName("Test Restaurant 3").user(user3).restaurantSelectionSession(session).build());

        session.setRestaurants(restaurants);

        RestaurantSelectionSession savedSession = sessionRepository.save(session);
        return savedSession.getId();
    }
}
