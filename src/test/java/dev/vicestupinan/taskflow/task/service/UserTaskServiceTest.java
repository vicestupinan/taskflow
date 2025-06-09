package dev.vicestupinan.taskflow.task.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.vicestupinan.taskflow.auth.AuthenticatedUserProvider;
import dev.vicestupinan.taskflow.exception.AccessDeniedException;
import dev.vicestupinan.taskflow.exception.ResourceNotFoundException;
import dev.vicestupinan.taskflow.task.dto.TaskRequest;
import dev.vicestupinan.taskflow.task.dto.TaskResponse;
import dev.vicestupinan.taskflow.task.mapper.TaskMapper;
import dev.vicestupinan.taskflow.task.model.Task;
import dev.vicestupinan.taskflow.task.model.TaskStatus;
import dev.vicestupinan.taskflow.task.repository.TaskRepository;
import dev.vicestupinan.taskflow.user.model.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@Epic("Tasks")
@ExtendWith(MockitoExtension.class)
public class UserTaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @InjectMocks
    private UserTaskService userTaskService;

    @Feature("Get Tasks")
    @Story("User retrieves their tasks")
    @DisplayName("Should return list of TaskResponses when user has tasks")
    @Description("This test verifies that the user can retrieve their tasks successfully.")
    @Test
    void getTasks_shouldReturnListOfTaskResponses_whenUserHasTasks() {

        User mockUser = User.builder().id(UUID.randomUUID()).build();

        Task mockTask1 = Task.builder().id(UUID.randomUUID()).title("Task 1").user(mockUser).build();
        Task mockTask2 = Task.builder().id(UUID.randomUUID()).title("Task 2").user(mockUser).build();

        List<Task> mockTasks = List.of(mockTask1, mockTask2);

        TaskResponse mockResponse1 = TaskResponse.builder().id(mockTask1.getId()).title(mockTask1.getTitle()).build();
        TaskResponse mockResponse2 = TaskResponse.builder().id(mockTask2.getId()).title(mockTask2.getTitle()).build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.findByUser(mockUser)).thenReturn(mockTasks);
        when(taskMapper.toResponse(mockTask1)).thenReturn(mockResponse1);
        when(taskMapper.toResponse(mockTask2)).thenReturn(mockResponse2);

        List<TaskResponse> result = userTaskService.listTasks();

        assertEquals(2, result.size());
        assertEquals(mockResponse1, result.get(0));
        assertEquals(mockResponse2, result.get(1));
        verify(taskRepository).findByUser(mockUser);
        verify(taskMapper).toResponse(mockTask1);
        verify(taskMapper).toResponse(mockTask2);
    }

    @Feature("Get Tasks")
    @Story("User attempts to retrieve tasks without any tasks")
    @DisplayName("Should return empty list when user has no tasks")
    @Description("This test verifies that the user receives an empty list when they have no tasks.")
    @Test
    void getTasks_shouldReturnEmptyList_whenUserHasNoTasks() {
        User mockUser = User.builder().id(UUID.randomUUID()).build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.findByUser(mockUser)).thenReturn(Collections.emptyList());

        List<TaskResponse> result = userTaskService.listTasks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository).findByUser(mockUser);
        verifyNoInteractions(taskMapper);
    }

    @Feature("Get Task")
    @Story("User gets a task by ID")
    @DisplayName("Should throw exception when user accesses another user's task")
    @Description("This test verifies that a user cannot access a task that belongs to another user.")
    @Test
    void getTaskById_shouldReturnTaskResponse_whenUserOwnsTask() {

        UUID mockTaskId = UUID.randomUUID();
        User mockUser = User.builder().id(UUID.randomUUID()).build();
        Task mockTask = Task.builder().id(mockTaskId).user(mockUser).build();
        TaskResponse response = TaskResponse.builder().id(mockTaskId).build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.of(mockTask));
        when(taskMapper.toResponse(mockTask)).thenReturn(response);

        TaskResponse result = userTaskService.getTaskById(mockTaskId);

        assertNotNull(result);
        assertEquals(mockTaskId, result.getId());
        verify(taskRepository).findById(mockTaskId);
        verify(taskMapper).toResponse(mockTask);
    }

    @Feature("Get Task")
    @Story("User attempts to get a task they do not own")
    @DisplayName("Should throw AccessDeniedException when user does not own the task")
    @Test
    void getTaskById_shouldThrowException_whenUserDoesNotOwnTask() {

        UUID mockTaskId = UUID.randomUUID();
        User mockOwner = User.builder().id(UUID.randomUUID()).build();
        User mockOtherUser = User.builder().id(UUID.randomUUID()).build();
        Task mockTask = Task.builder().id(mockTaskId).user(mockOwner).build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockOtherUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.of(mockTask));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userTaskService.getTaskById(mockTaskId));
        assertEquals("You do not have access to this task", exception.getMessage());
    }

    @Feature("Get Task")
    @Story("User attempts to get a task that does not exist")
    @DisplayName("Should throw ResourceNotFoundException when task does not exist")
    @Test
    void getTaskById_shouldThrowNotFound_whenTaskDoesNotExist() {

        UUID mockTaskId = UUID.randomUUID();
        User mockUser = User.builder().id(UUID.randomUUID()).build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userTaskService.getTaskById(mockTaskId));
        assertEquals("Task not found", exception.getMessage());
    }

    @Feature("Create Task")
    @Story("User creates a new task")
    @DisplayName("Should return TaskResponse when creating a task with valid request")
    @Test
    void createTask_ShouldReturnTaskResponse_WhenValidRequest() {
        TaskRequest mockRequest = new TaskRequest();
        mockRequest.setTitle("Test Task");
        mockRequest.setDescription("This is a test task");
        mockRequest.setTaskStatus(TaskStatus.TODO);

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());

        Task mockTask = Task.builder()
                .title(mockRequest.getTitle())
                .description(mockRequest.getDescription())
                .taskStatus(mockRequest.getTaskStatus())
                .user(mockUser)
                .build();

        TaskResponse mockResponse = TaskResponse.builder()
                .id(mockTask.getId())
                .title(mockTask.getTitle())
                .description(mockTask.getDescription())
                .taskStatus(mockTask.getTaskStatus())
                .build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.save(mockTask)).thenReturn(mockTask);
        when(taskMapper.toResponse(mockTask)).thenReturn(mockResponse);

        TaskResponse result = userTaskService.createTask(mockRequest);

        assertEquals(mockResponse, result);
        verify(taskRepository).save(mockTask);
        verify(taskMapper).toResponse(mockTask);
    }

    @Feature("Update Task")
    @Story("User updates an existing task")
    @DisplayName("Should update and return TaskResponse when user owns task")
    @Description("This test verifies that a user can update their task and receive the updated TaskResponse.")
    @Test
    void updateTask_shouldUpdateAndReturnTaskResponse_whenUserOwnsTask() {
        UUID mockTaskId = UUID.randomUUID();
        User mockUser = User.builder().id(UUID.randomUUID()).build();
        Task mockExistingTask = Task.builder().id(mockTaskId).user(mockUser).build();

        TaskRequest mockUpdateRequest = new TaskRequest();
        mockUpdateRequest.setTitle("Updated Title");
        mockUpdateRequest.setDescription("Updated Description");
        mockUpdateRequest.setTaskStatus(TaskStatus.IN_PROGRESS);

        TaskResponse expectedResponse = TaskResponse.builder()
                .id(mockTaskId)
                .title(mockUpdateRequest.getTitle())
                .description(mockUpdateRequest.getDescription())
                .taskStatus(mockUpdateRequest.getTaskStatus())
                .build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.of(mockExistingTask));
        when(taskMapper.toResponse(mockExistingTask)).thenReturn(expectedResponse);

        TaskResponse result = userTaskService.updateTask(mockTaskId, mockUpdateRequest);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(taskRepository).save(mockExistingTask);
        verify(taskMapper).toResponse(mockExistingTask);
    }

    @Feature("Update Task")
    @Story("User attempts to update a task they do not own")
    @DisplayName("Should throw AccessDeniedException when user does not own the task")
    @Description("This test verifies that a user cannot update a task that belongs to another user.")
    @Test
    void updateTask_shouldThrowAccessDenied_whenUserDoesNotOwnTask() {
        UUID mockTaskId = UUID.randomUUID();
        User mockOwner = User.builder().id(UUID.randomUUID()).build();
        User mockOtherUser = User.builder().id(UUID.randomUUID()).build();
        Task mockTask = Task.builder().id(mockTaskId).user(mockOwner).build();

        TaskRequest mockUpdateRequest = new TaskRequest();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockOtherUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.of(mockTask));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userTaskService.updateTask(mockTaskId, mockUpdateRequest));
        assertEquals("You do not have access to this task", exception.getMessage());
    }

    @Feature("Update Task")
    @Story("User attempts to update a task that does not exist")
    @DisplayName("Should throw ResourceNotFoundException when task does not exist")
    @Description("This test verifies that a user receives an error when trying to update a task that does not exist.")
    @Test
    void updateTask_shouldThrowNotFound_whenTaskDoesNotExist() {
        UUID mockTaskId = UUID.randomUUID();
        User mockUser = User.builder().id(UUID.randomUUID()).build();
        TaskRequest mockUpdateRequest = new TaskRequest();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userTaskService.updateTask(mockTaskId, mockUpdateRequest));
        assertEquals("Task not found", exception.getMessage());
    }

    @Feature("Delete Task")
    @Story("User deletes a task")
    @DisplayName("Should delete task when user owns it")
    @Description("This test verifies that a user can delete their task successfully.")
    @Test
    void deleteTask_shouldDeleteTask_whenUserOwnsTask() {
        UUID mockTaskId = UUID.randomUUID();
        User mockUser = User.builder().id(UUID.randomUUID()).build();
        Task mockTask = Task.builder().id(mockTaskId).user(mockUser).build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.of(mockTask));

        userTaskService.deleteTask(mockTaskId);

        verify(taskRepository).delete(mockTask);
    }

    @Feature("Delete Task")
    @Story("User attempts to delete a task they do not own")
    @DisplayName("Should throw AccessDeniedException when user does not own the task")
    @Description("This test verifies that a user cannot delete a task that belongs to another user.")
    @Test
    void deleteTask_shouldThrowAccessDenied_whenUserDoesNotOwnTask() {
        UUID mockTaskId = UUID.randomUUID();
        User mockOwner = User.builder().id(UUID.randomUUID()).build();
        User mockOtherUser = User.builder().id(UUID.randomUUID()).build();
        Task mockTask = Task.builder().id(mockTaskId).user(mockOwner).build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockOtherUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.of(mockTask));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userTaskService.deleteTask(mockTaskId));
        assertEquals("You do not have access to this task", exception.getMessage());
    }

    @Feature("Delete Task")
    @Story("User attempts to delete a task that does not exist")
    @DisplayName("Should throw ResourceNotFoundException when task does not exist")
    @Description("This test verifies that a user receives an error when trying to delete a task that does not exist.")
    @Test
    void deleteTask_shouldThrowNotFound_whenTaskDoesNotExist() {
        UUID mockTaskId = UUID.randomUUID();
        User mockUser = User.builder().id(UUID.randomUUID()).build();

        when(authenticatedUserProvider.getAuthenticatedUser()).thenReturn(mockUser);
        when(taskRepository.findById(mockTaskId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userTaskService.deleteTask(mockTaskId));
        assertEquals("Task not found", exception.getMessage());
    }
}
