package com.govtech.restaurant_selection.service;

import com.govtech.restaurant_selection.dto.RestaurantSelectionSessionDTO;
import com.govtech.restaurant_selection.modles.Restaurant;
import com.govtech.restaurant_selection.modles.RestaurantSelectionSession;
import com.govtech.restaurant_selection.modles.UserEntity;
import com.govtech.restaurant_selection.repository.RestaurantRepository;
import com.govtech.restaurant_selection.repository.SessionRepository;
import com.govtech.restaurant_selection.repository.UserRepository;
import com.govtech.restaurant_selection.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository  userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional
    public RestaurantSelectionSession createSession(RestaurantSelectionSessionDTO selectionSessionDTO) {
        UserEntity userEntity = userRepository.findByUsername(SecurityUtil.getSessionUser()).get();

        RestaurantSelectionSession selectionSession = RestaurantSelectionSession.builder()
                                                                                .active(true)
                                                                                .owner(userEntity)
                                                                                .name(selectionSessionDTO.getSessionName())
                                                                                .build();

        Restaurant restaurant = Restaurant.builder()
                                          .restaurantName(selectionSessionDTO.getRestaurantName())
                                          .user(userEntity)
                                          .restaurantSelectionSession(selectionSession)
                                          .build();

        selectionSession.setRestaurants(List.of(restaurant));

        return sessionRepository.save(selectionSession);
    }

    @Transactional
    public RestaurantSelectionSession closeSession(UUID sessionId) {
        RestaurantSelectionSession restaurantSelectionSession = sessionRepository.findById(sessionId).orElseThrow();
        if (!restaurantSelectionSession.getOwner().getUsername()
                .equals(SecurityUtil.getSessionUser()))
            return null;
        restaurantSelectionSession.setActive(false);

        int selectedRestaurantIndex = RandomGenerator.getDefault().nextInt(restaurantSelectionSession.getRestaurants().size());

        Restaurant restaurant = restaurantSelectionSession.getRestaurants().get(selectedRestaurantIndex);
        restaurantSelectionSession.setSelectedRestaurant(restaurant);

        restaurantSelectionSession = sessionRepository.save(restaurantSelectionSession);

        return updateSelectedRestaurantInList(restaurantSelectionSession);
    }

    public RestaurantSelectionSession getSession(UUID sessionId) {
        RestaurantSelectionSession restaurantSelectionSession = sessionRepository.findById(sessionId).orElseThrow();
        if (!restaurantSelectionSession.isActive())
            return updateSelectedRestaurantInList(restaurantSelectionSession);

        return restaurantSelectionSession;
    }

    public List<RestaurantSelectionSession> getActiveSessions() {
        return sessionRepository.findByActiveOrderByCreatedOnDesc(true);
    }

    public List<RestaurantSelectionSession> getInactiveSessions() {
        return sessionRepository.findByActiveOrderByCreatedOnDesc(false);
    }

    private RestaurantSelectionSession updateSelectedRestaurantInList(RestaurantSelectionSession restaurantSelectionSession) {
        Restaurant restaurant = restaurantSelectionSession.getSelectedRestaurant();
        List<Restaurant> restaurants = restaurantSelectionSession.getRestaurants()
                                                                 .stream()
                                                                 .peek(restaurant1 -> {
                                                                    if(restaurant1.getId().equals(restaurant.getId()))
                                                                        restaurant1.setSelected(true);
                                                                 })
                                                                 .sorted(Comparator.comparing(Restaurant::isSelected).reversed())
                                                                 .toList();

        restaurantSelectionSession.setRestaurants(restaurants);
        return restaurantSelectionSession;
    }

}
