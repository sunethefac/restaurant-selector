package com.govtech.restaurant_selection.controller;

import com.govtech.restaurant_selection.dto.RestaurantDTO;
import com.govtech.restaurant_selection.dto.RestaurantSelectionSessionDTO;
import com.govtech.restaurant_selection.modles.RestaurantSelectionSession;
import com.govtech.restaurant_selection.security.SecurityUtil;
import com.govtech.restaurant_selection.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<RestaurantSelectionSession> restaurantSelectionSessions = sessionService.getActiveSessions();
        model.addAttribute("restaurantSelectionSessions", restaurantSelectionSessions);
        return "index";
    }

    @GetMapping("/session/create")
    public String getCreateSessionForm(Model model) {
        model.addAttribute("restaurantSelectionSession", new RestaurantSelectionSessionDTO());
        return "create_session";
    }

    @PostMapping("/session/create/new")
    public String createSession(@Valid @ModelAttribute("restaurantSelectionSession") RestaurantSelectionSessionDTO selectionSessionDTO,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("restaurantSelectionSession", selectionSessionDTO);
            return "create_session";
        }

        RestaurantSelectionSession session = sessionService.createSession(selectionSessionDTO);
        return "redirect:/sessions/" + session.getId() + "?success";
    }

    @GetMapping("/sessions/previous")
    public String getPreviousSessions(Model model) {
        List<RestaurantSelectionSession> restaurantSelectionSessions = sessionService.getInactiveSessions();
        model.addAttribute("restaurantSelectionSessions", restaurantSelectionSessions);
        return "previous_sessions";
    }

    @GetMapping("/sessions/{sessionId}")
    public String getSession(@PathVariable("sessionId") UUID sessionId, Model model) {
        RestaurantSelectionSession restaurantSelectionSession = sessionService.getSession(sessionId);
        model.addAttribute("restaurantSelectionSession", restaurantSelectionSession);
        model.addAttribute("restaurant", new RestaurantDTO());
        if (restaurantSelectionSession.isActive()) {
            model.addAttribute("hasUserAlreadyAddedRestaurant", restaurantSelectionSession.getRestaurants().stream()
                    .anyMatch(restaurant ->
                            Objects.equals(SecurityUtil.getSessionUser(), restaurant.getUser().getUsername())
                    )
            );
            model.addAttribute("allowedToCloseSession", restaurantSelectionSession.getOwner().getUsername().equals(SecurityUtil.getSessionUser()));
        } else {
            model.addAttribute("allowedToCloseSession", false);
            model.addAttribute("hasUserAlreadyAddedRestaurant", true);
        }
        return "session_details";
    }

    @PostMapping("/sessions/{sessionId}")
    public String closeSession(@PathVariable("sessionId") UUID sessionId, Model model) {
        RestaurantSelectionSession restaurantSelectionSession = sessionService.closeSession(sessionId);

        if (restaurantSelectionSession == null)
            return "redirect:/session/closing/error?notAllowed";

        return "redirect:/sessions/" + restaurantSelectionSession.getId() + "?restaurantSelected";
    }

    @GetMapping("/session/closing/error")
    public String errorPageForClosingNotAllowed() {
        return "session_closing_error";
    }

}
