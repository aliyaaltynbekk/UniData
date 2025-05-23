package org.unidata1.service;

import org.unidata1.model.Message;
import org.unidata1.model.User;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    Message sendMessage(Message message);

    Message replyToMessage(Long originalMessageId, Message reply);

    Optional<Message> getMessageById(Long id);

    List<Message> getMessagesBySender(User sender);

    List<Message> getMessagesByReceiver(User receiver);

    List<Message> getUnreadMessages(User receiver);

    List<Message> getAllMessages();

    List<Message> getMessagesByType(Message.MessageType type);

    void markAsRead(Long messageId);

    void markAllAsRead(User receiver);

    void deleteMessage(Long id);

    void deleteAllMessagesByUser(User user);

    long countUnreadMessages(User receiver);

    long countTotalMessages();

    boolean hasUnreadMessages(User receiver);

    void broadcastMessage(@jakarta.annotation.Nonnull Message message);
}