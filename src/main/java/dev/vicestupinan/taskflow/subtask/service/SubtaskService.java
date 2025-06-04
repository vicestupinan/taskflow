package dev.vicestupinan.taskflow.subtask.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dev.vicestupinan.taskflow.auth.AuthenticatedUserProvider;
import dev.vicestupinan.taskflow.exception.AccessDeniedException;
import dev.vicestupinan.taskflow.exception.ResourceNotFoundException;
import dev.vicestupinan.taskflow.subtask.dto.SubtaskResponse;
import dev.vicestupinan.taskflow.subtask.mapper.SubtaskMapper;
import dev.vicestupinan.taskflow.subtask.repository.SubtaskRepository;
import dev.vicestupinan.taskflow.task.model.Task;
import dev.vicestupinan.taskflow.task.repository.TaskRepository;
import dev.vicestupinan.taskflow.user.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;
    private final SubtaskMapper subtaskMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public List<SubtaskResponse> listSubtasks(UUID taskId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have access to this task");
        }
        return subtaskRepository.findByTaskId(taskId)
                .stream()
                .map(subtaskMapper::toResponse)
                .collect(Collectors.toList());
    }
}
