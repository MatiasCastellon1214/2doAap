package com.app.toDoApp.service.impl;

import com.app.toDoApp.dto.entrada.TaskEntradaDTO;
import com.app.toDoApp.dto.modificacion.TaskModificacionEntradaDTO;
import com.app.toDoApp.dto.salida.TaskSalidaDTO;
import com.app.toDoApp.dto.salida.UserSalidaDTO;
import com.app.toDoApp.entity.Task;
import com.app.toDoApp.entity.User;
import com.app.toDoApp.exceptions.ResourceNotFoundException;
import com.app.toDoApp.repository.TaskRepository;
import com.app.toDoApp.repository.UserRepository;
import com.app.toDoApp.security.UserPrincipal;
import com.app.toDoApp.service.ITaskService;
import com.app.toDoApp.utils.JsonPrinter;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {

    private final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public TaskSalidaDTO createTask(TaskEntradaDTO taskDTO) throws ResourceNotFoundException {
        LOGGER.info("Creating task with Data: {}", JsonPrinter.toString(taskDTO));

        // 1. Find User
        User user = userRepository.findById(taskDTO.getUserId())
                .orElseThrow(() -> {
                    LOGGER.warn("User with ID {} not found", taskDTO.getUserId());
                    return new ResourceNotFoundException("User with ID " + taskDTO.getUserId() + " not found");

                });


        // 2. Mapping DTO to entity
        Task task = modelMapper.map(taskDTO, Task.class);

        // 3. Establishing automatic relationships and values
        task.setUser(user);
        task.setCreatedAt(LocalDate.now()); // Automatic date

        // 4. Save to database
        Task savedTask = taskRepository.save(task);
        LOGGER.info("Task created successfully with ID: {}", savedTask.getId());

        // 5. Prepare response
        TaskSalidaDTO response = modelMapper.map(savedTask, TaskSalidaDTO.class);
        response.setUser(mapUserToUserSalidaDTO(user));

        LOGGER.info("Returning created task: {}", JsonPrinter.toString(response));
        return response;
    }

    @Override
    public List<TaskSalidaDTO> findTasksByUserId(Long userId) {

        LOGGER.info("Fetching tasks for user with ID: {}", userId);

        List<Task> tasks = taskRepository.findByUserId(userId);
        LOGGER.info("Found {} tasks for user ID: {}", tasks.size(), userId);
        return tasks.stream().map(task -> {
            TaskSalidaDTO dto = modelMapper.map(task, TaskSalidaDTO.class);
            dto.setUser(mapUserToUserSalidaDTO(task.getUser()));
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<TaskSalidaDTO> listTasks() {
        List<Task> tasks = taskRepository.findAll();
        LOGGER.info("Listing all tasks - Total count: {}", tasks.size());

        return tasks.stream()
                .map(task -> {
                    TaskSalidaDTO dto = modelMapper.map(task, TaskSalidaDTO.class);
                    dto.setUser(mapUserToUserSalidaDTO(task.getUser()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TaskSalidaDTO findTaskById(Long id) throws ResourceNotFoundException {

        LOGGER.info("Searching for task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("Task with ID {} not found", id);
                    return new ResourceNotFoundException("Task with ID " + id + " not found");
                });

        TaskSalidaDTO response = modelMapper.map(task, TaskSalidaDTO.class);
        response.setUser(mapUserToUserSalidaDTO(task.getUser()));

        LOGGER.info("Found task: {}", JsonPrinter.toString(response));
        return response;
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, Authentication authentication) throws ResourceNotFoundException {

        Long currentUserId = ((UserPrincipal) authentication.getPrincipal()).getId();

        LOGGER.info("Attempting to delete task ID: {} by user ID: {}", taskId, currentUserId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    LOGGER.warn("Task with ID {} not found", taskId);
                    return new ResourceNotFoundException("Task with ID " + taskId + " not found");
                });

        // Verify that the authenticated user is the owner
        if (!task.getUser().getId().equals(currentUserId)) {
            LOGGER.warn("User ID {} does not own task ID {}, deletion denied", currentUserId, taskId);
            throw new ResourceNotFoundException("You do not have permission to delete this task");
        }

        taskRepository.deleteById(taskId);
        LOGGER.info("Task with ID {} deleted successfully", taskId);
    }



    @Override
    @Transactional
    public TaskSalidaDTO updateTask(TaskModificacionEntradaDTO taskDTO, Authentication authentication) throws ResourceNotFoundException {

        Long currentUserId = ((UserPrincipal) authentication.getPrincipal()).getId();

        LOGGER.info("Updating task with data: {}", JsonPrinter.toString(taskDTO));

        // 1. Verificar que la tarea existe
        Task existingTask = taskRepository.findById(taskDTO.getId())
                .orElseThrow(() -> {
                    LOGGER.warn("Task with ID {} not found", taskDTO.getId());
                    return new ResourceNotFoundException("Task with ID " + taskDTO.getId() + " not found");
                });

        // 2. Verify that the task belongs to the authenticated user
        if (!existingTask.getUser().getId().equals(currentUserId)) {
            LOGGER.warn("User ID {} is not authorized to update task ID {}", currentUserId, taskDTO.getId());
            throw new ResourceNotFoundException("You do not have permission to update this task");
        }

        // 3. Maintain editable fields
        if (taskDTO.getDescription() != null) {
            existingTask.setDescription(taskDTO.getDescription());
        }

        // 4. Save changes
        Task updatedTask = taskRepository.save(existingTask);

        // 5. Prepare response
        TaskSalidaDTO response = modelMapper.map(updatedTask, TaskSalidaDTO.class);
        response.setUser(mapUserToUserSalidaDTO(updatedTask.getUser()));

        LOGGER.info("Task updated successfully: {}", JsonPrinter.toString(response));
        return response;
    }


    // Método auxiliar para mapear User a UserSalidaDTO
    private UserSalidaDTO mapUserToUserSalidaDTO(User user) {
        if (user == null) return null;
        UserSalidaDTO userDTO = new UserSalidaDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    @Override
    @Transactional
    public TaskSalidaDTO updateTaskStatus(Long taskId, Boolean completed, Long userId) throws ResourceNotFoundException {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        task.setCompleted(completed);
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskSalidaDTO.class);
    }
}