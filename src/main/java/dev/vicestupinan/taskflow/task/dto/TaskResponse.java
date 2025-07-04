package dev.vicestupinan.taskflow.task.dto;

import java.util.UUID;

import dev.vicestupinan.taskflow.task.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus taskStatus;
}
