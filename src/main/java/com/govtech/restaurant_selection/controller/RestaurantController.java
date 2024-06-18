package com.govtech.restaurant_selection.controller;


import com.govtech.restaurant_selection.dto.RestaurantDTO;
import com.govtech.restaurant_selection.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/restaurant/add/{sessionId}")
    public String addRestaurant(@PathVariable("sessionId") String sessionId,
                                @Valid @ModelAttribute("restaurant") RestaurantDTO restaurantDTO,
                                BindingResult result) {

        if (result.hasErrors())
            return "redirect:/sessions/" + sessionId + "?restaurantValidationError";

        restaurantService.proposeARestaurant(UUID.fromString(sessionId), restaurantDTO);

        return "redirect:/sessions/"+ sessionId + "?restaurantAdded";
    }

}
