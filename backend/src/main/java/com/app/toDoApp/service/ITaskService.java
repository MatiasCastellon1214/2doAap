package com.app.toDoApp.service;

import com.app.toDoApp.dto.entrada.TaskEntradaDTO;
import com.app.toDoApp.dto.modificacion.TaskModificacionEntradaDTO;
import com.app.toDoApp.dto.salida.TaskSalidaDTO;
import com.app.toDoApp.exceptions.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ITaskService {

    TaskSalidaDTO createTask(TaskEntradaDTO task) throws ResourceNotFoundException;


    List<TaskSalidaDTO> listTasks();

    TaskSalidaDTO findTaskById(Long id) throws ResourceNotFoundException;

    void deleteTask(Long taskId, Authentication authentication) throws ResourceNotFoundException;

    TaskSalidaDTO updateTask(TaskModificacionEntradaDTO taskDTO, Authentication authentication) throws ResourceNotFoundException;

    List<TaskSalidaDTO> findTasksByUserId(Long userId);

    TaskSalidaDTO updateTaskStatus(Long taskId, Boolean completed, Long userId) throws ResourceNotFoundException;

}
