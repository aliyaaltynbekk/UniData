package org.unidata1.service;

import org.unidata1.model.AuditLog;
import org.unidata1.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditService {

    AuditLog logAction(AuditLog auditLog);

    AuditLog logUserAction(User user, String actionType, String description, boolean successful);

    AuditLog logObjectAction(User user, String actionType, String description,
                             boolean successful, String objectType, Long objectId);

    List<AuditLog> getAllLogs();

    List<AuditLog> getLogsByUser(User user);

    List<AuditLog> getLogsByActionType(String actionType);

    List<AuditLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLog> getSuccessfulLogs();

    List<AuditLog> getFailedLogs();

    void deleteLogsOlderThan(LocalDateTime date);

    long countTotalLogs();

    long countLogsByUser(User user);

    String exportLogsToFile(String filePath);
}