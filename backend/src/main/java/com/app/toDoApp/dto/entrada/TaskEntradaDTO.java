package com.app.toDoApp.dto.entrada;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntradaDTO {

    private String description;

    private Long userId;

}