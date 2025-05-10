package com.app.toDoApp.dto.salida;

import com.app.toDoApp.entity.Task;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSalidaDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TaskSalidaDTO> tasksSalidaDTO;

}
