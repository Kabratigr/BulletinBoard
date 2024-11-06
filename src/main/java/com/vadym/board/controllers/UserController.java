package com.vadym.board.controllers;

import com.vadym.board.models.User;
import com.vadym.board.models.enums.UserStatus;
import com.vadym.board.services.ReCaptchaService;
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

    private final ReCaptchaService reCaptchaService;

    @GetMapping("/login")
    public String login() {
        return "login-page";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration-page";
    }

    @PostMapping("/registration")
    public String createUser(@RequestParam("g-recaptcha-response") String reCaptchaResponse,
            User user, Model model) {
        if (!reCaptchaService.validateRecaptcha(reCaptchaResponse)) {
            model.addAttribute("captchaError", "Invalid Captcha Validation");
            return "registration-page";
        }
        UserStatus errorCode = userService.createUser(user);
        switch (errorCode) {
            case EMAIL_EXISTS:
                model.addAttribute("emailError", "A user with " +
                        user.getEmail() + " already exists.");
                return "registration-page";
            case NICKNAME_EXISTS:
                model.addAttribute("nickNameError", "A user with " +
                        user.getNickname() + " already exists.");
                return "registration-page";
            case SUCCESS: return "redirect:/login";
            default:
                model.addAttribute("unknownError", "Unknown error");
                return "registration-page";
        }
    }

    @GetMapping("/user/{user}")
    public String userProfile(@PathVariable(name = "user") User user, Principal principal, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("announcements", user.getAnnouncements());
        model.addAttribute("numberOfSubscribers", user.getSubscribers().size());
        model.addAttribute("numberOfSubscriptions", user.getSubscriptions().size());
        model.addAttribute("isSubscriber", user.getSubscribers().contains(utilityService.getUserByPrincipal(principal)));
        model.addAttribute("authenticatedUser", utilityService.getUserByPrincipal(principal));
        return "user-details";
    }

    @GetMapping("/subscribe/{user}")
    public String subscribe(@PathVariable(name = "user") User user, Principal principal) {
        userService.subscribe(utilityService.getUserByPrincipal(principal), user);
        return "redirect:/user/" + user.getId();
    }

    @GetMapping("/unsubscribe/{user}")
    public String unSubscribe(@PathVariable(name = "user") User user, Principal principal) {
        userService.unSubscribe(utilityService.getUserByPrincipal(principal), user);
        return "redirect:/user/" + user.getId();
    }

    @GetMapping("/{type}/{user}/list")
    public String subscriptionsList(@PathVariable String type,
                                    @PathVariable User user,
                                    @RequestParam(name = "nickname", required = false) String nickname,
                                    Model model, Principal principal) {
        model.addAttribute("userChannel", user);
        model.addAttribute("type", type);
        model.addAttribute("authenticatedUser", utilityService.getUserByPrincipal(principal));
        if ("subscriptions".equals(type)) {
            model.addAttribute("userSubscriptions", user.getSubscriptions());
        } else {
            model.addAttribute("userSubscribers", user.getSubscribers());
        }
        model.addAttribute("userList", userService.findSubscriptions(type, nickname, user));
        return "subscriptions-list";
    }

    @GetMapping("/users/list")
    public String usersList(@RequestParam(name = "nickname", required = false) String nickname,
                            Model model, Principal principal) {
        model.addAttribute("user", utilityService.getUserByPrincipal(principal));
        model.addAttribute("allUsers", userService.getUsersByNickname(nickname));
        return "user-list";
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
                                  @RequestParam(name = "nickname", required = false) String nickname,
                                  @RequestParam(name = "email", required = false) String email,
                                  @RequestParam(name = "password", required = false) String password,
                                  @PathVariable(name = "user") User user) {
        String userEmailBeforeUpdate = user.getEmail();
        userService.updateProfile(user, nickname, name, surname, email, password);
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
