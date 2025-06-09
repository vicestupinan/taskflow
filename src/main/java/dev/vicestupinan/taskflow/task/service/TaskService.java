package dev.vicestupinan.taskflow.task.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dev.vicestupinan.taskflow.task.dto.TaskRequest;
import dev.vicestupinan.taskflow.task.dto.TaskResponse;

public interface TaskService {

    Page<TaskResponse> listTasks(Pageable pageable);

    TaskResponse getTaskById(UUID id);

    TaskResponse createTask(TaskRequest request);

    TaskResponse updateTask(UUID id, TaskRequest request);

    void deleteTask(UUID id);
}
