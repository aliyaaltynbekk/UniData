package org.unidata1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unidata1.model.Message;
import org.unidata1.model.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrderBySentTimeDesc(User sender);
    List<Message> findByReceiverOrderBySentTimeDesc(User receiver);
    List<Message> findByReceiverAndReadFalseOrderBySentTimeDesc(User receiver);
    List<Message> findByMessageType(Message.MessageType messageType);
    long countByReceiverAndReadFalse(User receiver);
}