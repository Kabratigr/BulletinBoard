package com.vadym.board.controllers;

import com.vadym.board.models.User;
import com.vadym.board.services.UserService;
import com.vadym.board.services.UtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UtilityService utilityService;

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
    public String userProfile(@PathVariable(name = "user") User user, Principal principal, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("announcements", user.getAnnouncements());
        model.addAttribute("authenticatedUser", utilityService.getUserByPrincipal(principal));
        return "user-details";
    }

    @PreAuthorize("#user.email.equals(authentication.principal.email)")
    @GetMapping("/user/{user}/edit")
    public String editUserProfileForm(@PathVariable(name = "user") User user, Model model) {
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PreAuthorize("#user.email.equals(authentication.principal.email)")
    @PostMapping("/user/{user}/edit")
    public String editUserProfile(@RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "surname", required = false) String surname,
                                  @RequestParam(name = "email", required = false) String email,
                                  @RequestParam(name = "password", required = false) String password,
                                  @PathVariable(name = "user") User user) {
        String userEmailBeforeUpdate = user.getEmail();
        userService.updateProfile(user, name, surname, email, password);
        if (!userEmailBeforeUpdate.equals(email)) {
            return "redirect:/login";
        }
        return "redirect:/user/{user}";
    }

    @GetMapping("/activate/{code}")
    public String activateUser(@PathVariable String code) {
        userService.activateUser(code);
        return "redirect:/login";
    }
}
