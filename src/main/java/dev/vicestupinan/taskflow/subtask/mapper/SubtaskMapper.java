package dev.vicestupinan.taskflow.subtask.mapper;

import org.springframework.stereotype.Component;

import dev.vicestupinan.taskflow.subtask.dto.SubtaskRequest;
import dev.vicestupinan.taskflow.subtask.dto.SubtaskResponse;
import dev.vicestupinan.taskflow.subtask.model.Subtask;

@Component
public class SubtaskMapper {

    public SubtaskResponse toResponse(Subtask subtask) {
        return SubtaskResponse.builder()
                .id(subtask.getId())
                .title(subtask.getTitle())
                .description(subtask.getDescription())
                .taskStatus(subtask.getTaskStatus())
                .build();
    }

    public Subtask toEntity(SubtaskRequest request) {
        return Subtask.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .taskStatus(request.getTaskStatus())
                .build();
    }
}
