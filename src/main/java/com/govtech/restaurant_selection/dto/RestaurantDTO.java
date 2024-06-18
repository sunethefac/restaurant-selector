package com.govtech.restaurant_selection.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    @NotEmpty(message = "Restaurant Name should not be empty")
    private String name;
}
