package dev.vicestupinan.taskflow.task.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.vicestupinan.taskflow.auth.AuthenticatedUserProvider;
import dev.vicestupinan.taskflow.exception.ResourceNotFoundException;
import dev.vicestupinan.taskflow.task.dto.TaskRequest;
import dev.vicestupinan.taskflow.task.dto.TaskResponse;
import dev.vicestupinan.taskflow.task.mapper.TaskMapper;
import dev.vicestupinan.taskflow.task.model.Task;
import dev.vicestupinan.taskflow.task.repository.TaskRepository;
import dev.vicestupinan.taskflow.user.model.User;
import lombok.RequiredArgsConstructor;

@Service("adminTaskService")
@RequiredArgsConstructor
public class AdminTaskService implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Page<TaskResponse> listTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    public TaskResponse getTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(taskMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    @Override
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

    @Override
    public TaskResponse updateTask(UUID id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setTaskStatus(request.getTaskStatus());
        taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    @Override
    public void deleteTask(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
    }
}
