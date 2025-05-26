package com.app.toDoApp.repository;

import com.app.toDoApp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    // Nueva consulta para buscar por ID y estado completado
    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.completed = :completed")
    Optional<Task> findByIdAndCompleted(@Param("id") Long id, @Param("completed") boolean completed);

}