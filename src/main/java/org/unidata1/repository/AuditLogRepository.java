package org.unidata1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unidata1.model.AuditLog;
import org.unidata1.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUser(User user);

    List<AuditLog> findByActionType(String actionType);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AuditLog> findBySuccessful(boolean successful);

    List<AuditLog> findByObjectTypeAndObjectId(String objectType, Long objectId);

    long countByUser(User user);

    List<AuditLog> findByUserAndActionType(User user, String actionType);

    List<AuditLog> findByUserAndTimestampBetween(User user, LocalDateTime start, LocalDateTime end);

    List<AuditLog> findByUserAndSuccessful(User user, boolean successful);

    List<AuditLog> findByTimestampBefore(LocalDateTime timestamp);

    void deleteByTimestampBefore(LocalDateTime timestamp);
}