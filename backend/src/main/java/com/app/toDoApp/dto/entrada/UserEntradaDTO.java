package com.app.toDoApp.dto.entrada;

import com.app.toDoApp.entity.Task;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntradaDTO {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private List<TaskEntradaDTO> tasksEntradaDTO;
}
