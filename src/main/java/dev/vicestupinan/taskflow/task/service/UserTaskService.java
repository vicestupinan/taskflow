package dev.vicestupinan.taskflow.task.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.vicestupinan.taskflow.auth.AuthenticatedUserProvider;
import dev.vicestupinan.taskflow.exception.AccessDeniedException;
import dev.vicestupinan.taskflow.exception.ResourceNotFoundException;
import dev.vicestupinan.taskflow.task.dto.TaskRequest;
import dev.vicestupinan.taskflow.task.dto.TaskResponse;
import dev.vicestupinan.taskflow.task.mapper.TaskMapper;
import dev.vicestupinan.taskflow.task.model.Task;
import dev.vicestupinan.taskflow.task.repository.TaskRepository;
import dev.vicestupinan.taskflow.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("userTaskService")
@RequiredArgsConstructor
public class UserTaskService implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> listTasks() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Listing tasks for user: {}", user.getId());
        return taskRepository.findByUser(user)
                .stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Fetching task {} for user {}", id, user.getId());
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to access unauthorized task {}", user.getId(), id);
            throw new AccessDeniedException("You do not have access to this task");
        }
        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Creating new task for user: {}", user.getId());
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .taskStatus(request.getTaskStatus())
                .user(user)
                .build();
        taskRepository.save(task);
        log.info("Task {} created successfully for user {}", task.getId(), user.getId());
        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID id, TaskRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Updating task {} for user {}", id, user.getId());
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to update unauthorized task {}", user.getId(), id);
            throw new AccessDeniedException("You do not have access to this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setTaskStatus(request.getTaskStatus());
        taskRepository.save(task);
        log.info("Task {} updated successfully by user {}", task.getId(), user.getId());
        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public void deleteTask(UUID id) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        log.info("Deleting task {} for user {}", id, user.getId());
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to delete unauthorized task {}", user.getId(), id);
            throw new AccessDeniedException("You do not have access to this task");
        }

        taskRepository.delete(task);
        log.info("Task {} deleted successfully by user {}", id, user.getId());
    }
}
