package com.govtech.restaurant_selection.service;

import com.govtech.restaurant_selection.dto.RestaurantDTO;
import com.govtech.restaurant_selection.repository.UserRepository;
import com.govtech.restaurant_selection.modles.Restaurant;
import com.govtech.restaurant_selection.modles.RestaurantSelectionSession;
import com.govtech.restaurant_selection.modles.UserEntity;
import com.govtech.restaurant_selection.repository.RestaurantRepository;
import com.govtech.restaurant_selection.repository.SessionRepository;
import com.govtech.restaurant_selection.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void proposeARestaurant(UUID sessionId, RestaurantDTO restaurantDTO){
        RestaurantSelectionSession restaurantSelectionSession = sessionRepository.findById(sessionId).orElseThrow();
        if (restaurantSelectionSession.isActive()) {
            UserEntity user = userRepository.findByUsername(SecurityUtil.getSessionUser()).orElseThrow();
            Restaurant restaurant = Restaurant.builder()
                                              .restaurantSelectionSession(restaurantSelectionSession)
                                              .restaurantName(restaurantDTO.getName())
                                              .user(user)
                                              .build();
            restaurantRepository.save(restaurant);
        }
    }

}
