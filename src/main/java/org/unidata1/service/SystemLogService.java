package org.unidata1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unidata1.model.SystemLog;
import org.unidata1.repository.SystemLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SystemLogService {

    private final SystemLogRepository systemLogRepository;

    @Autowired
    public SystemLogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    public SystemLog createLog(SystemLog log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }
        return systemLogRepository.save(log);
    }

    public Page<SystemLog> getAllLogs(Pageable pageable) {
        return systemLogRepository.findAll(pageable);
    }

    public Optional<SystemLog> getLogById(Long id) {
        return systemLogRepository.findById(id);
    }

    public Page<SystemLog> getLogsByLevel(SystemLog.LogLevel level, Pageable pageable) {
        return systemLogRepository.findByLevelOrderByTimestampDesc(level, pageable);
    }

    public Page<SystemLog> getLogsByUsername(String username, Pageable pageable) {
        return systemLogRepository.findByUsernameContainingIgnoreCaseOrderByTimestampDesc(username, pageable);
    }

    public Page<SystemLog> getLogsByAction(String action, Pageable pageable) {
        return systemLogRepository.findByActionContainingIgnoreCaseOrderByTimestampDesc(action, pageable);
    }

    public Page<SystemLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return systemLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end, pageable);
    }

    public Page<SystemLog> getLogsByFilters(SystemLog.LogLevel level, String username, String action,
                                            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return systemLogRepository.findByFilters(level, username, action, startDate, endDate, pageable);
    }

    @Transactional
    public void clearAllLogs() {
        systemLogRepository.deleteAll();
    }

    public List<SystemLog> getRecentLogs() {
        return systemLogRepository.findTop10ByOrderByTimestampDesc();
    }
}