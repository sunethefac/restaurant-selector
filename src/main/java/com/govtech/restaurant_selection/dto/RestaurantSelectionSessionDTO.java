package com.govtech.restaurant_selection.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestaurantSelectionSessionDTO {

    @NotEmpty(message = "Session Name should not be empty")
    private String sessionName;
    @NotEmpty(message = "Restaurant Name should not be empty")
    private String restaurantName;

}
