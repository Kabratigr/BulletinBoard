package com.vadym.board.controllers;

import com.vadym.board.models.User;
import com.vadym.board.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login-page";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration-page";
    }

    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage",
                    "User with " + user.getEmail() + " already exists");
            return "registration-page";
        }
        return "redirect:/login";
    }

    @GetMapping("/user/{user}")
    public String userProfile(@PathVariable(name = "user") User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("announcements", user.getAnnouncements());
        return "user-details";
    }

    @GetMapping("/activate/{code}")
    public String activateUser(@PathVariable String code) {
        userService.activateUser(code);
        return "redirect:/login";
    }
}
