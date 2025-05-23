package org.unidata1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.unidata1.model.Message;
import org.unidata1.model.User;
import org.unidata1.service.AuthService;
import org.unidata1.service.MessageService;
import org.unidata1.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final MessageService messageService;
    private final AuthService authService;

    public UserController(UserService userService, MessageService messageService, AuthService authService) {
        this.userService = userService;
        this.messageService = messageService;
        this.authService = authService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
    if (principal != null) {
        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Менің профилім");
        });
        return "user/profile";
    }
    return "redirect:/auth/login";
}

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
    if (principal != null) {
        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Профильді өңдеу");
        });
        return "user/edit-profile";
    }
    return "redirect:/auth/login";
}

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user, Principal principal) {
    if (principal != null) {
        userService.getUserByUsername(principal.getName()).ifPresent(currentUser -> {
            currentUser.setFullName(user.getFullName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            userService.updateUser(currentUser);
        });
    }
    return "redirect:/user/profile";
}

    @GetMapping("/messages")
    public String viewMessages(Model model, Principal principal) {
    if (principal != null) {
        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            List<Message> receivedMessages = messageService.getMessagesByReceiver(user);
            List<Message> sentMessages = messageService.getMessagesBySender(user);
            boolean hasUnread = messageService.hasUnreadMessages(user);

            model.addAttribute("receivedMessages", receivedMessages);
            model.addAttribute("sentMessages", sentMessages);
            model.addAttribute("hasUnread", hasUnread);
            model.addAttribute("pageTitle", "Хабарламалар");
        });
        return "user/messages";
    }
    return "redirect:/auth/login";
}

    @GetMapping("/password/change")
    public String changePasswordForm(Model model) {
        model.addAttribute("pageTitle", "Құпия сөзді өзгерту");
        return "user/change-password";
    }

    @PostMapping("/password/change")
    public String changePassword(
        @RequestParam String oldPassword,
        @RequestParam String newPassword,
        Principal principal) {
    if (principal != null) {
        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            authService.changePassword(user.getId(), oldPassword, newPassword);
        });
    }
    return "redirect:/user/profile?passwordChanged";
}
}