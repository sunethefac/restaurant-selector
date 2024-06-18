package com.govtech.restaurant_selection.repository;

import com.govtech.restaurant_selection.modles.RestaurantSelectionSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<RestaurantSelectionSession, UUID> {

    List<RestaurantSelectionSession> findByActiveOrderByCreatedOnDesc(boolean active);

}
