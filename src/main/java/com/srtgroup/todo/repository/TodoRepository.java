package com.srtgroup.todo.repository;

import com.srtgroup.todo.enums.Priority;
import com.srtgroup.todo.enums.Status;
import com.srtgroup.todo.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByStatus(Status status, Pageable pageable);

    Page<Todo> findByPriority(Priority priority, Pageable pageable);

    @Query("""
            SELECT t FROM Todo t
            WHERE (:keyword IS NULL OR :keyword = ''
                OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:status IS NULL OR t.status = :status)
            AND (:priority IS NULL OR t.priority = :priority)
            """)
    Page<Todo> findByFilters(
            @Param("keyword")  String keyword,
            @Param("status")   Status status,
            @Param("priority") Priority priority,
            Pageable pageable);

    long countByStatus(Status status);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.deadline < :now AND t.status <> 'DONE'")
    long countOverdue(@Param("now") LocalDateTime now);
}
