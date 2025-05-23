package org.unidata1.service;

import org.unidata1.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface NotificationService {

    void sendEmailNotification(User recipient, String subject, String content);

    void sendSmsNotification(User recipient, String content);

    void sendSystemNotification(User recipient, String title, String content);

    void sendBulkEmailNotification(List<User> recipients, String subject, String content);

    void sendBulkSmsNotification(List<User> recipients, String content);

    void sendBulkSystemNotification(List<User> recipients, String title, String content);

    void scheduleNotification(User recipient, String subject, String content,
                              String notificationType, LocalDateTime scheduledTime);

    boolean isEmailEnabled(User user);

    boolean isSmsEnabled(User user);

    boolean isSystemNotificationEnabled(User user);

    void enableEmailNotifications(User user);

    void disableEmailNotifications(User user);

    void enableSmsNotifications(User user);

    void disableSmsNotifications(User user);

    Map<String, Boolean> getUserNotificationPreferences(User user);

    void updateUserNotificationPreferences(User user, Map<String, Boolean> preferences);
}