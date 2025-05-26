package com.app.toDoApp.dto.salida;

import com.app.toDoApp.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskSalidaDTO {
    private Long id;

    private String description;

    private Boolean completed;

    private LocalDate createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserSalidaDTO user;
}
