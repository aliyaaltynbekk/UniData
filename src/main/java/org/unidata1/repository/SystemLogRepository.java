package org.unidata1.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.unidata1.model.SystemLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    Page<SystemLog> findByLevelOrderByTimestampDesc(SystemLog.LogLevel level, Pageable pageable);

    Page<SystemLog> findByUsernameContainingIgnoreCaseOrderByTimestampDesc(String username, Pageable pageable);

    Page<SystemLog> findByActionContainingIgnoreCaseOrderByTimestampDesc(String action, Pageable pageable);

    Page<SystemLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT s FROM SystemLog s WHERE " +
            "(:level IS NULL OR s.level = :level) AND " +
            "(:username IS NULL OR LOWER(s.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
            "(:action IS NULL OR LOWER(s.action) LIKE LOWER(CONCAT('%', :action, '%'))) AND " +
            "(:startDate IS NULL OR s.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR s.timestamp <= :endDate) " +
            "ORDER BY s.timestamp DESC")
    Page<SystemLog> findByFilters(
            @Param("level") SystemLog.LogLevel level,
            @Param("username") String username,
            @Param("action") String action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    void deleteAll();

    List<SystemLog> findTop10ByOrderByTimestampDesc();
}