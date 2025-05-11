package com.app.toDoApp.controller;

import com.app.toDoApp.dto.entrada.UserEntradaDTO;
import com.app.toDoApp.dto.modificacion.UserModificacionEntradaDTO;
import com.app.toDoApp.dto.salida.TaskSalidaDTO;
import com.app.toDoApp.dto.salida.UserSalidaDTO;
import com.app.toDoApp.entity.User;
import com.app.toDoApp.exceptions.ResourceNotFoundException;
import com.app.toDoApp.repository.UserRepository;
import com.app.toDoApp.security.UserPrincipal;
import com.app.toDoApp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final ModelMapper modelMapper;
    private final IUserService userService;

    private final UserRepository userRepository;

    @GetMapping("/searchId")
    public String searchUserById(Model model, @RequestParam Long id){

        UserSalidaDTO user = userService.findUserById(id);

        model.addAttribute("First name", user.getFirstName());
        model.addAttribute("Las name", user.getLastName());

        return "User";

    }

    @GetMapping("/list")
    public ResponseEntity<List<UserSalidaDTO>> listUsers(){
        return new ResponseEntity<>(userService.listUsers(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<UserSalidaDTO> createnewUser(@RequestBody UserEntradaDTO user){
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws ResourceNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Search user with PathVariable
    @GetMapping("{id}")
    public ResponseEntity<UserSalidaDTO> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserSalidaDTO> updateUser(@RequestBody UserModificacionEntradaDTO user){
        return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskSalidaDTO>> getUserTasks(@PathVariable Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<TaskSalidaDTO> tasks = user.getTasks().stream()
                .map(task -> modelMapper.map(task, TaskSalidaDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/me")
    public ResponseEntity<UserSalidaDTO> getCurrentUser(Authentication authentication) throws ResourceNotFoundException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserSalidaDTO usuario = userService.findUserById(userPrincipal.getId());
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }


}
