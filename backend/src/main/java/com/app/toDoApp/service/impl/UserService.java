package com.app.toDoApp.service.impl;

import com.app.toDoApp.dto.entrada.UserEntradaDTO;
import com.app.toDoApp.dto.modificacion.UserModificacionEntradaDTO;
import com.app.toDoApp.dto.salida.UserSalidaDTO;
import com.app.toDoApp.entity.User;
import com.app.toDoApp.repository.UserRepository;
import com.app.toDoApp.service.IUserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import com.app.toDoApp.utils.JsonPrinter;


@Service
public class UserService implements IUserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    public UserService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserSalidaDTO createUser(UserEntradaDTO user) {
        LOGGER.info("UserEntradaDTO: " + JsonPrinter.toString(user));
        User userEntity = modelMapper.map(user, User.class);
        LOGGER.info("Mapped User entity: " + JsonPrinter.toString(userEntity));
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        User userToPersist = userRepository.save(userEntity);

        UserSalidaDTO userSalidaDTO = modelMapper.map(userToPersist, UserSalidaDTO.class);
        LOGGER.info("UserSalidaDTO: " + JsonPrinter.toString(userSalidaDTO));

        return userSalidaDTO;
    }

    @Override
    public List<UserSalidaDTO> listUsers() {
        List<UserSalidaDTO> userSalidaDTOS = userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserSalidaDTO.class))
                .toList();
        LOGGER.info("List of all Users: " + JsonPrinter.toString(userSalidaDTOS));
        return userSalidaDTOS;
    }

    @Override
    public UserSalidaDTO findUserById(Long id) {
        User userSearched = userRepository.findById(id).orElse(null);
        UserSalidaDTO userFound = null;

        if(userSearched != null){
            userFound =  modelMapper.map(userSearched, UserSalidaDTO.class);
            LOGGER.info("User found: {}", JsonPrinter.toString(userFound));
        } else LOGGER.error("The id is not registered in the database.");

        return userFound;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserSalidaDTO updateUser(UserModificacionEntradaDTO user) {
        User userReceived = modelMapper.map(user, User.class);
        User usertUpdate = userRepository.findById(userReceived.getId()).orElse(null);

        UserSalidaDTO userSalidaDTO = null;

        if (usertUpdate != null) {
            usertUpdate = userReceived;
            userRepository.save(usertUpdate);

            userSalidaDTO = modelMapper.map(usertUpdate, UserSalidaDTO.class);
            LOGGER.warn("User updated: {}", JsonPrinter.toString(userSalidaDTO));

        } else {
            LOGGER.error("It was not possible to update the user because he/she is not in our database.");
            //lanzar excepcion correspondiente
        }


        return userSalidaDTO;
    }

    @Override
    public boolean existsByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        LOGGER.info("Checking if email exists: {} - Result: {}", email, exists);
        return exists;
    }

    private void configureMapping(){
        modelMapper.typeMap(UserEntradaDTO.class, User.class)
                .addMappings(mapper -> {
                    mapper.skip(User::setId);
                    mapper.skip(User::setTasks);
                });

        modelMapper.typeMap(User.class, UserSalidaDTO.class);


    }
}
