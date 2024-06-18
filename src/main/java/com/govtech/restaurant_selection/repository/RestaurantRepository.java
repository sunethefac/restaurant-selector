package com.govtech.restaurant_selection.repository;

import com.govtech.restaurant_selection.modles.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
