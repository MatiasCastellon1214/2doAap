package com.app.toDoApp;

import com.app.toDoApp.entity.User;
import com.app.toDoApp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class ToDoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToDoAppApplication.class, args);
	}


	@Bean
	CommandLineRunner encodeExistingPasswords(UserRepository userRepository, PasswordEncoder encoder) {
		return args -> {
			List<User> users = userRepository.findAll();
			for (User user : users) {
				if (!user.getPassword().startsWith("$2a$")) { // Encrypt only if unencrypted
					user.setPassword(encoder.encode(user.getPassword()));
					userRepository.save(user);
				}
			}
		};
	}




}
