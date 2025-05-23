package org.unidata1.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unidata1.model.Message;
import org.unidata1.model.User;
import org.unidata1.repository.MessageRepository;
import org.unidata1.service.MessageService;
import org.unidata1.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    public MessageServiceImpl(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Message sendMessage(Message message) {
        message.setSentTime(LocalDateTime.now());
        message.setRead(false);
        if (message.getMessageType() == null) {
            message.setMessageType(Message.MessageType.PERSONAL);
        }
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public Message replyToMessage(Long originalMessageId, Message reply) {
        Optional<Message> original = messageRepository.findById(originalMessageId);
        if (original.isPresent()) {
            reply.setSentTime(LocalDateTime.now());
            reply.setRead(false);
            reply.setSubject("Re: " + original.get().getSubject());
            reply.setParentMessage(original.get());
            if (reply.getMessageType() == null) {
                reply.setMessageType(Message.MessageType.PERSONAL);
            }
            return messageRepository.save(reply);
        }
        return null;
    }

    @Override
    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> getMessagesBySender(User sender) {
        return messageRepository.findBySenderOrderBySentTimeDesc(sender);
    }

    @Override
    public List<Message> getMessagesByReceiver(User receiver) {
        return messageRepository.findByReceiverOrderBySentTimeDesc(receiver);
    }

    @Override
    public List<Message> getUnreadMessages(User receiver) {
        return messageRepository.findByReceiverAndReadFalseOrderBySentTimeDesc(receiver);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getMessagesByType(Message.MessageType type) {
        return messageRepository.findByMessageType(type);
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            message.setReadTime(LocalDateTime.now());
            messageRepository.save(message);
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(User receiver) {
        List<Message> unreadMessages = messageRepository.findByReceiverAndReadFalseOrderBySentTimeDesc(receiver);
        LocalDateTime now = LocalDateTime.now();
        for (Message message : unreadMessages) {
            message.setRead(true);
            message.setReadTime(now);
        }
        messageRepository.saveAll(unreadMessages);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllMessagesByUser(User user) {
        List<Message> messagesToDelete = messageRepository.findBySenderOrderBySentTimeDesc(user);
        messagesToDelete.addAll(messageRepository.findByReceiverOrderBySentTimeDesc(user));
        messageRepository.deleteAll(messagesToDelete);
    }

    @Override
    public long countUnreadMessages(User receiver) {
        return messageRepository.countByReceiverAndReadFalse(receiver);
    }

    @Override
    public long countTotalMessages() {
        return messageRepository.count();
    }

    @Override
    public boolean hasUnreadMessages(User receiver) {
        return messageRepository.countByReceiverAndReadFalse(receiver) > 0;
    }


    public void broadcastMessage(Message message) {
        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            if (!user.getId().equals(message.getSender().getId())) {
                Message copy = new Message();
                copy.setSender(message.getSender());
                copy.setReceiver(user);
                copy.setSubject(message.getSubject());
                copy.setContent(message.getContent());
                copy.setMessageType(Message.MessageType.ANNOUNCEMENT);
                messageRepository.save(copy);
            }
        }
    }

}