package com.app.toDoApp.dto.modificacion;

import com.app.toDoApp.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskModificacionEntradaDTO {

    private Long id;

    private String description;

    private boolean completed;

    private Long userId;

}
