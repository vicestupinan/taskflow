package dev.vicestupinan.taskflow.task.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dev.vicestupinan.taskflow.auth.AuthenticatedUserProvider;
import dev.vicestupinan.taskflow.exception.AccessDeniedException;
import dev.vicestupinan.taskflow.task.dto.TaskRequest;
import dev.vicestupinan.taskflow.task.dto.TaskResponse;
import dev.vicestupinan.taskflow.task.mapper.TaskMapper;
import dev.vicestupinan.taskflow.task.model.Task;
import dev.vicestupinan.taskflow.task.repository.TaskRepository;
import dev.vicestupinan.taskflow.user.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public TaskResponse getTaskById(UUID id) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have access to this task");
        }
        return taskMapper.toResponse(task);
    }

    public TaskResponse createTask(TaskRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .taskStatus(request.getTaskStatus())
                .user(user)
                .build();
        taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    public List<TaskResponse> listTasks() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return taskRepository.findByUser(user)
                .stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }
}
