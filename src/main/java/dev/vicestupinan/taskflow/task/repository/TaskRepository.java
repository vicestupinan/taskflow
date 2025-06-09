package dev.vicestupinan.taskflow.task.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.vicestupinan.taskflow.task.model.Task;
import dev.vicestupinan.taskflow.user.model.User;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    Page<Task> findByUser(User user, Pageable pageable);
}
