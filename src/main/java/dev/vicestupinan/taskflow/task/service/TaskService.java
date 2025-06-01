package dev.vicestupinan.taskflow.task.service;

import java.util.List;
import java.util.UUID;

import dev.vicestupinan.taskflow.task.dto.TaskRequest;
import dev.vicestupinan.taskflow.task.dto.TaskResponse;

public interface TaskService {

    List<TaskResponse> listTasks();

    TaskResponse getTaskById(UUID id);

    TaskResponse createTask(TaskRequest request);

    TaskResponse updateTask(UUID id, TaskRequest request);

    void deleteTask(UUID id);
}
