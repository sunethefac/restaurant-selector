package com.govtech.restaurant_selection.controller;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.govtech.restaurant_selection.dto.RestaurantSelectionSessionDTO;
import com.govtech.restaurant_selection.modles.Restaurant;
import com.govtech.restaurant_selection.modles.RestaurantSelectionSession;
import com.govtech.restaurant_selection.modles.UserEntity;
import com.govtech.restaurant_selection.repository.SessionRepository;
import com.govtech.restaurant_selection.security.SecurityUtil;
import com.govtech.restaurant_selection.service.SessionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebMvcTest(SessionController.class)
public class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private RestaurantSelectionSession mockSession;

    @MockBean
    private SessionRepository sessionRepository;

    @Test
    void loadingHome() throws Exception {
        mockMvc.perform(get("/home").with(SecurityMockMvcRequestPostProcessors.user("testUser")))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("restaurantSelectionSessions"));
    }

    @Test
    void createSessionForm() throws Exception {
        mockMvc.perform(
                        get("/session/create")
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("create_session"))
                        .andExpect(model().attributeExists("restaurantSelectionSession"));
    }

    @Test
    void createSessionShouldGiveErrorMessagesWhenSessionRequestObjectIsMissing() throws Exception {
        mockMvc.perform(
                        post("/session/create/new")
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(view().name("create_session"))
                        .andExpect(model().attributeExists("restaurantSelectionSession"))
                        .andExpect(model().hasErrors())
                        .andExpect(model().errorCount(2));
    }

    @Test
    void createSessionShouldCreateSessionWhenRequestIsValid() throws Exception {

        UUID sessionId = UUID.randomUUID();

        when(sessionService.createSession(ArgumentMatchers.any(RestaurantSelectionSessionDTO.class)))
                .thenReturn(RestaurantSelectionSession.builder().id(sessionId).build());

        mockMvc.perform(
                        post("/session/create/new")
                                        .contentType(APPLICATION_FORM_URLENCODED)
                                        .param("sessionName","Test Session")
                                        .param("restaurantName", "Test Restaurant")
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                        )
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/sessions/" + sessionId + "?success"))
                        .andExpect(model().hasNoErrors());
    }

    @Test
    void getPreviousSessions() throws Exception {

        mockMvc.perform(get("/sessions/previous").with(SecurityMockMvcRequestPostProcessors.user("testUser")))
                .andExpect(status().isOk())
                .andExpect(view().name("previous_sessions"))
                .andExpect(model().attributeExists("restaurantSelectionSessions"));
    }

    @Test
    void getSessionWhenSessionIsActiveAndUserIsTheOwner() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UserEntity user = UserEntity.builder().username("testUser").build();

        when(mockSession.getId()).thenReturn(sessionId);
        when(mockSession.getOwner()).thenReturn(user);
        when(mockSession.isActive()).thenReturn(true);

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser");
        }
        when(sessionService.getSession(sessionId)).thenReturn(mockSession);

        mockMvc.perform(
                        get("/sessions/" + sessionId)
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("session_details"))
                .andExpect(model().attributeExists("restaurantSelectionSession"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attribute("allowedToCloseSession", true))
                .andExpect(model().attribute("hasUserAlreadyAddedRestaurant", false))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void getSessionWhenSessionIsActiveAndUserIsTheNotTheOwnerAndHaveNotAddedRestaurant() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UserEntity user = UserEntity.builder().username("testUser1").build();

        when(mockSession.getId()).thenReturn(sessionId);
        when(mockSession.getOwner()).thenReturn(user);
        when(mockSession.isActive()).thenReturn(true);

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser");
        }
        when(sessionService.getSession(sessionId)).thenReturn(mockSession);

        mockMvc.perform(
                        get("/sessions/" + sessionId)
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("session_details"))
                .andExpect(model().attributeExists("restaurantSelectionSession"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attribute("allowedToCloseSession", false))
                .andExpect(model().attribute("hasUserAlreadyAddedRestaurant", false))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void getSessionWhenSessionIsActiveAndUserIsTheNotTheOwnerAndHaveAddedRestaurant() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UserEntity owner = UserEntity.builder().username("testUser1").build();
        UserEntity user = UserEntity.builder().username("testUser").build();

        when(mockSession.getId()).thenReturn(sessionId);
        when(mockSession.getOwner()).thenReturn(owner);
        when(mockSession.isActive()).thenReturn(true);
        when(mockSession.getRestaurants()).thenReturn(List.of(Restaurant.builder().user(user).build()));

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser");
        }
        when(sessionService.getSession(sessionId)).thenReturn(mockSession);

        mockMvc.perform(
                        get("/sessions/" + sessionId)
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("session_details"))
                .andExpect(model().attributeExists("restaurantSelectionSession"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attribute("allowedToCloseSession", false))
                .andExpect(model().attribute("hasUserAlreadyAddedRestaurant", true))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void getSessionWhenSessionIsInactive() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UserEntity owner = UserEntity.builder().username("testUser1").build();

        when(mockSession.getOwner()).thenReturn(owner);
        when(mockSession.isActive()).thenReturn(false);

        when(sessionService.getSession(sessionId)).thenReturn(mockSession);

        mockMvc.perform(
                        get("/sessions/" + sessionId)
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("session_details"))
                .andExpect(model().attributeExists("restaurantSelectionSession"))
                .andExpect(model().attributeExists("restaurant"))
                .andExpect(model().attribute("allowedToCloseSession", false))
                .andExpect(model().attribute("hasUserAlreadyAddedRestaurant", true))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void closeSessionWhenUserIsNotOwner() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UserEntity owner = UserEntity.builder().username("testUser1").build();

        when(mockSession.getOwner()).thenReturn(owner);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(mockSession));

        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser");
        }

        mockMvc.perform(
                        post("/sessions/" + sessionId)
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/session/closing/error?notAllowed"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void closeSessionWhenUserIsTheOwner() throws Exception {
        UUID sessionId = UUID.randomUUID();

        when(mockSession.getId()).thenReturn(sessionId);
        when(sessionService.closeSession(sessionId)).thenReturn(mockSession);

        mockMvc.perform(
                        post("/sessions/" + sessionId)
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/sessions/" + sessionId + "?restaurantSelected"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void getErrorPage() throws Exception {
        mockMvc.perform(
                        get("/session/closing/error")
                                .with(SecurityMockMvcRequestPostProcessors.user("testUser"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("session_closing_error"))
                .andExpect(model().hasNoErrors());
    }


}
