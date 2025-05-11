package com.app.toDoApp.controller;

import com.app.toDoApp.dto.entrada.TaskEntradaDTO;
import com.app.toDoApp.dto.modificacion.TaskModificacionEntradaDTO;
import com.app.toDoApp.dto.salida.TaskSalidaDTO;
import com.app.toDoApp.entity.User;
import com.app.toDoApp.exceptions.ResourceNotFoundException;
import com.app.toDoApp.repository.TaskRepository;
import com.app.toDoApp.repository.UserRepository;
import com.app.toDoApp.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.app.toDoApp.security.UserPrincipal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;
    @Autowired
    private ITaskService taskService;

    public TaskController(UserRepository userRepository, TaskRepository taskRepository, ITaskService taskService) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
    }

    @GetMapping("/searchId")
    public String searchTaskById(Model model, @RequestParam Long id) throws ResourceNotFoundException {

        TaskSalidaDTO task = taskService.findTaskById(id);

        model.addAttribute("Description", task.getDescription());
        model.addAttribute("Created at", task.getCreatedAt());

        return "Task";

    }

    @GetMapping("/list")
    public ResponseEntity<List<TaskSalidaDTO>> listTasks(){
        return new ResponseEntity<>(taskService.listTasks(), HttpStatus.OK);
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskSalidaDTO>> getMyTasks(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();

        List<TaskSalidaDTO> userTasks = taskService.findTasksByUserId(userId);
        return new ResponseEntity<>(userTasks, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<TaskSalidaDTO> createnewTask(@RequestBody TaskEntradaDTO taskDTO, Authentication authentication) throws ResourceNotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        taskDTO.setUserId(userPrincipal.getId());  // Solo seteás el ID del usuario
        TaskSalidaDTO newTask = taskService.createTask(taskDTO);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) throws ResourceNotFoundException {
        taskService.deleteTask(id, authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Search user with PathVariable
    @GetMapping("{id}")
    public ResponseEntity<TaskSalidaDTO> getTaskById(@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(taskService.findTaskById(id), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<TaskSalidaDTO> updateTask(@RequestBody TaskModificacionEntradaDTO task, Authentication authentication) throws ResourceNotFoundException {
        return new ResponseEntity<>(taskService.updateTask(task, authentication), HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskSalidaDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> update,
            Authentication authentication) throws ResourceNotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Boolean completed = update.get("completed");

        if (completed == null) {
            throw new IllegalArgumentException("El campo 'completed' es requerido");
        }

        return ResponseEntity.ok(taskService.updateTaskStatus(id, completed, userPrincipal.getId()));
    }

}
