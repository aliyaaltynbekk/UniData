package org.unidata1.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.unidata1.model.Message;
import org.unidata1.model.User;
import org.unidata1.service.MessageService;
import org.unidata1.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("")
    public String messages(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

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

    @GetMapping("/inbox")
    public String inbox(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            List<Message> messages = messageService.getMessagesByReceiver(user);
            model.addAttribute("messages", messages);
            model.addAttribute("pageTitle", "Кіріс хабарламалар");
        });

        return "user/messages/inbox";
    }

    @GetMapping("/sent")
    public String sent(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            List<Message> messages = messageService.getMessagesBySender(user);
            model.addAttribute("messages", messages);
            model.addAttribute("pageTitle", "Жіберілген хабарламалар");
        });

        return "user/messages/sent";
    }

    @GetMapping("/compose")
    public String composeMessage(Model model) {
        model.addAttribute("message", new Message());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("pageTitle", "Жаңа хабарлама");
        model.addAttribute("messageTypes", Message.MessageType.values());

        return "user/messages/compose";
    }

    @PostMapping("/send")
    public String sendMessage(@ModelAttribute Message message,
                              @RequestParam Long receiverId,
                              Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(sender -> {
            userService.getUserById(receiverId).ifPresent(receiver -> {
                message.setSender(sender);
                message.setReceiver(receiver);
                messageService.sendMessage(message);
            });
        });

        return "redirect:/messages/sent";
    }

    @GetMapping("/{id}")
    public String viewMessage(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        Optional<Message> messageOpt = messageService.getMessageById(id);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            model.addAttribute("message", message);
            model.addAttribute("pageTitle", "Хабарлама");

            userService.getUserByUsername(principal.getName()).ifPresent(user -> {
                if (message.getReceiver().getId().equals(user.getId()) && !message.isRead()) {
                    messageService.markAsRead(id);
                }
            });

            return "user/messages/view";
        }

        return "redirect:/messages/inbox";
    }

    @GetMapping("/{id}/reply")
    public String replyToMessage(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        Optional<Message> originalMessage = messageService.getMessageById(id);
        if (originalMessage.isPresent()) {
            Message reply = new Message();
            reply.setReceiver(originalMessage.get().getSender());
            model.addAttribute("originalMessage", originalMessage.get());
            model.addAttribute("replyMessage", reply);
            model.addAttribute("pageTitle", "Жауап беру");

            return "user/messages/reply";
        }

        return "redirect:/messages/inbox";
    }

    @PostMapping("/{id}/reply")
    public String sendReply(@PathVariable Long id, @ModelAttribute Message reply, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(sender -> {
            reply.setSender(sender);
            messageService.replyToMessage(id, reply);
        });

        return "redirect:/messages/sent";
    }

    @PostMapping("/{id}/delete")
    public String deleteMessage(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        messageService.getMessageById(id).ifPresent(message -> {
            userService.getUserByUsername(principal.getName()).ifPresent(user -> {
                if (message.getSender().getId().equals(user.getId()) ||
                        message.getReceiver().getId().equals(user.getId())) {
                    messageService.deleteMessage(id);
                }
            });
        });

        return "redirect:/messages/inbox";
    }

    @PostMapping("/mark-all-read")
    public String markAllAsRead(Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            messageService.markAllAsRead(user);
        });

        return "redirect:/messages/inbox";
    }
}