package com.app.toDoApp.dto.patch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusUpdateDTO {

    @NotNull(message = "The 'completed' field is required.")
    private Boolean completed;
}
