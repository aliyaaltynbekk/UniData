package org.unidata1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private String subject;
    private String content;

    private LocalDateTime sentTime;
    private LocalDateTime readTime;

    private boolean read;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;

    public LocalDateTime getTimestamp() {
        return this.sentTime;
    }

    public void setTimestamp(LocalDateTime now) {
        this.sentTime = now;
    }

    public enum MessageType {
        PERSONAL("Жеке"),
        GROUP("Топтық"),
        SYSTEM("Жүйелік"),
        ANNOUNCEMENT("Хабарландыру");

        private final String displayName;

        MessageType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Message() {
    }

    public Message(Long id, User sender, User receiver, String subject, String content) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.sentTime = LocalDateTime.now();
        this.read = false;
        this.messageType = MessageType.PERSONAL;
    }

    public Message(Long id, User sender, User receiver, String subject,
                   String content, MessageType messageType) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.sentTime = LocalDateTime.now();
        this.read = false;
        this.messageType = messageType;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSentTime() { return sentTime; }
    public void setSentTime(LocalDateTime sentTime) { this.sentTime = sentTime; }

    public LocalDateTime getReadTime() { return readTime; }
    public void setReadTime(LocalDateTime readTime) { this.readTime = readTime; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public Message getParentMessage() { return parentMessage; }
    public void setParentMessage(Message parentMessage) { this.parentMessage = parentMessage; }

    public void markAsRead() {
        this.read = true;
        this.readTime = LocalDateTime.now();
    }



    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + (sender != null ? sender.getFullName() : "null") +
                ", receiver=" + (receiver != null ? receiver.getFullName() : "null") +
                ", subject='" + subject + '\'' +
                ", sentTime=" + sentTime +
                ", read=" + read +
                '}';
    }
}
