package com.vadym.board.controllers;

import com.vadym.board.models.User;
import com.vadym.board.models.enums.Role;
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
import java.util.Map;

@Controller
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    private final UtilityService utilityService;

    @GetMapping("/admin")
    public String adminPanel(Principal principal, Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("admin", utilityService.getUserByPrincipal(principal));
        return "admin-page";
    }

    @PostMapping("/user/ban/{id}")
    public String banAndUnbanUser(@PathVariable(name = "id") Long id) {
        userService.banAndUnbanUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/user/edit/{user}")
    public String userChangeRole(@PathVariable(name = "user") User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user-change-role";
    }

    @PostMapping("/user/edit")
    public String userChangeRole(@RequestParam(name = "userId") User user,
                                 @RequestParam Map<String, String> form) {
        userService.userChangeRole(user, form);
        return "redirect:/admin";
    }
}
