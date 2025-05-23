package org.unidata1.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.unidata1.model.Message;
import org.unidata1.model.Role;
import org.unidata1.model.SystemLog;
import org.unidata1.model.User;
import org.unidata1.service.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final DataService dataService;
    private final RoleService roleService;
    private final SystemLogService systemLogService;
    private final MessageService messageService;


    public AdminController(UserService userService, RoleService roleService, DataService dataService, SystemLogService systemLogService, MessageService messageService) {
        this.userService = userService;
        this.roleService = roleService;
        this.dataService = dataService;
        this.systemLogService = systemLogService;
        this.messageService = messageService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("activeUsers", userService.countActiveUsers());
        model.addAttribute("totalStudents", dataService.countStudents());
        model.addAttribute("totalTeachers", dataService.countTeachers());
        model.addAttribute("pageTitle", "Басқару тақтасы");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Пайдаланушылар тізімі");
        return "admin/users";
    }

    @GetMapping("/roles")
    public String listRoles(Model model) {
        model.addAttribute("roleTypes", Role.RoleType.values());
        model.addAttribute("pageTitle", "Рөлдер");
        return "admin/roles";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("pageTitle", "Жүйе параметрлері");
        return "admin/settings";
    }
    @GetMapping("")
    public String adminIndex() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String viewUser(@PathVariable Long id, Model model) {
        return "user/profile";
    }

    @GetMapping("/admin/users/edit")
    public String editUser(@PathVariable Long id, Model model) {
        return "user/edit-profile";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Қате: пайдаланушы жойылмады.");
        }
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
            return "admin/messages";
        }
        return "redirect:/auth/login";
    }
    @GetMapping("/logs")
    public String logs(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       @RequestParam(value = "level", required = false) SystemLog.LogLevel level,
                       @RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "startDate", required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                       @RequestParam(value = "endDate", required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        Page<SystemLog> logs;
        if (level != null || search != null || startDate != null || endDate != null) {
            logs = systemLogService.getLogsByFilters(level, search, search, startDate, endDate, pageable);
        } else {
            logs = systemLogService.getAllLogs(pageable);
        }

        model.addAttribute("pageTitle", "Жүйе журналдары");
        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logs.getTotalPages());
        model.addAttribute("level", level);
        model.addAttribute("search", search);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "logs";
    }

    @PostMapping("/logs/clear")
    @ResponseBody
    public ResponseEntity<String> clearLogs() {
        systemLogService.clearAllLogs();
        return ResponseEntity.ok("Барлық журналдар сәтті тазаланды");
    }

    @GetMapping("/logs/export")
    public ResponseEntity<byte[]> exportLogs() {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Уақыт,Деңгей,Пайдаланушы,Әрекет,IP мекенжайы,Сеанс ID,Мәлімет\n");

        Page<SystemLog> logs = systemLogService.getAllLogs(PageRequest.of(0, 1000, Sort.by("timestamp").descending()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (SystemLog log : logs) {
            csvBuilder.append(log.getTimestamp().format(formatter)).append(",");
            csvBuilder.append(log.getLevel()).append(",");
            csvBuilder.append(log.getUsername() != null ? log.getUsername() : "").append(",");
            csvBuilder.append(log.getAction()).append(",");
            csvBuilder.append(log.getIpAddress() != null ? log.getIpAddress() : "").append(",");
            csvBuilder.append(log.getSessionId() != null ? log.getSessionId() : "").append(",");
            csvBuilder.append(log.getDetails() != null ? "\"" + log.getDetails().replace("\"", "\"\"") + "\"" : "").append("\n");
        }

        byte[] csvBytes = csvBuilder.toString().getBytes();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=logs.csv")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(csvBytes.length))
                .body(csvBytes);
    }
    @GetMapping("/messages/inbox")
    public String adminInbox(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            List<Message> messages = messageService.getMessagesByReceiver(user);
            model.addAttribute("messages", messages);
            model.addAttribute("pageTitle", "Кіріс хабарламалар");
        });

        return "admin/messages/inbox";
    }

    @GetMapping("/messages/sent")
    public String adminSent(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            List<Message> messages = messageService.getMessagesBySender(user);
            model.addAttribute("messages", messages);
            model.addAttribute("pageTitle", "Жіберілген хабарламалар");
        });

        return "admin/messages/sent";
    }

    @GetMapping("/messages/compose")
    public String adminComposeMessage(Model model) {
        model.addAttribute("message", new Message());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("pageTitle", "Жаңа хабарлама");
        model.addAttribute("messageTypes", Message.MessageType.values());

        return "admin/messages/compose";
    }

    @PostMapping("/messages/send")
    public String adminSendMessage(@ModelAttribute Message message,
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

        return "redirect:/admin/messages/sent";
    }

    @GetMapping("/messages/{id}")
    public String adminViewMessage(@PathVariable Long id, Model model, Principal principal) {
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

            return "admin/messages/view";
        }

        return "redirect:/admin/messages/inbox";
    }

    @GetMapping("/messages/{id}/reply")
    public String adminReplyToMessage(@PathVariable Long id, Model model, Principal principal) {
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

            return "admin/messages/reply";
        }

        return "redirect:/admin/messages/inbox";
    }

    @PostMapping("/messages/{id}/reply")
    public String adminSendReply(@PathVariable Long id, @ModelAttribute Message reply, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(sender -> {
            reply.setSender(sender);
            messageService.replyToMessage(id, reply);
        });

        return "redirect:/admin/messages/sent";
    }

    @PostMapping("/messages/{id}/delete")
    public String adminDeleteMessage(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        messageService.getMessageById(id).ifPresent(message -> {
            userService.getUserByUsername(principal.getName()).ifPresent(user -> {
                if (message.getSender().getId().equals(user.getId()) ||
                        message.getReceiver().getId().equals(user.getId()) ||
                        user.getRoles().stream().anyMatch(role -> role.getRoleType() == Role.RoleType.ADMIN)) {
                    messageService.deleteMessage(id);
                }
            });
        });

        return "redirect:/admin/messages/inbox";
    }

    @PostMapping("/messages/mark-all-read")
    public String adminMarkAllAsRead(Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        userService.getUserByUsername(principal.getName()).ifPresent(user -> {
            messageService.markAllAsRead(user);
        });

        return "redirect:/admin/messages/inbox";
    }

    @GetMapping("/messages/all")
    public String adminViewAllMessages(Model model) {
        List<Message> allMessages = messageService.getAllMessages();
        model.addAttribute("messages", allMessages);
        model.addAttribute("pageTitle", "Барлық хабарламалар");
        return "admin/messages/all";
    }

    @PostMapping("/messages/{id}/broadcast")
    public String adminBroadcastMessage(@PathVariable Long id) {
        messageService.getMessageById(id).ifPresent(messageService::broadcastMessage);
        return "redirect:/admin/messages/all";
    }
}
