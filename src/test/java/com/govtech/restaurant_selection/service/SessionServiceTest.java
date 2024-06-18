package com.govtech.restaurant_selection.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.govtech.restaurant_selection.modles.RestaurantSelectionSession;
import com.govtech.restaurant_selection.modles.UserEntity;
import com.govtech.restaurant_selection.repository.SessionRepository;
import com.govtech.restaurant_selection.security.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void testCloseSessionWhenUserIsNotOwner() {
        UUID sessionId = UUID.randomUUID();

        RestaurantSelectionSession savedSession = RestaurantSelectionSession.builder()
                        .active(true)
                        .owner(UserEntity.builder().username("owner").build())
                        .build();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(savedSession));
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getSessionUser).thenReturn("testUser");
            RestaurantSelectionSession session = sessionService.closeSession(sessionId);
            assertNull(session);
        }

    }

}
