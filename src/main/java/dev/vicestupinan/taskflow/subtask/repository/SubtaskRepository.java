package dev.vicestupinan.taskflow.subtask.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.vicestupinan.taskflow.subtask.model.Subtask;
import dev.vicestupinan.taskflow.task.model.TaskStatus;

public interface SubtaskRepository extends JpaRepository<Subtask, UUID> {

    List<Subtask> findByTaskId(UUID taskId);

    List<Subtask> findByTaskIdAndTaskStatus(UUID taskId, TaskStatus taskStatus);

    Optional<Subtask> findByIdAndTaskId(UUID id, UUID taskId);

}
