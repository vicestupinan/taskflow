package dev.vicestupinan.taskflow.task.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.vicestupinan.taskflow.task.model.Task;
import dev.vicestupinan.taskflow.user.model.User;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByUser(User user);
}
