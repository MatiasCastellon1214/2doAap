package com.app.toDoApp.service;

import com.app.toDoApp.dto.entrada.UserEntradaDTO;
import com.app.toDoApp.dto.modificacion.UserModificacionEntradaDTO;
import com.app.toDoApp.dto.salida.UserSalidaDTO;

import java.util.List;

public interface IUserService {

    UserSalidaDTO createUser(UserEntradaDTO user);

    List<UserSalidaDTO> listUsers();

    UserSalidaDTO findUserById(Long id);

    void deleteUser(Long id);

    UserSalidaDTO updateUser(UserModificacionEntradaDTO user);

}
