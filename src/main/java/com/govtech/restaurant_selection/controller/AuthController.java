package com.govtech.restaurant_selection.controller;

import com.govtech.restaurant_selection.dto.UserDTO;
import com.govtech.restaurant_selection.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String getUserRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register/user")
    public String register(@Valid @ModelAttribute("user") UserDTO user, BindingResult result, Model model) {
        if (userService.isUserExists(user.getUsername()))
            result.rejectValue("username", "error", "User already exists with the given username");
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login?success";
    }

}
