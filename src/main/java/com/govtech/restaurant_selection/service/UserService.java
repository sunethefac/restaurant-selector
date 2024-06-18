package com.govtech.restaurant_selection.service;

import com.govtech.restaurant_selection.dto.UserDTO;
import com.govtech.restaurant_selection.modles.UserEntity;
import com.govtech.restaurant_selection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(UserDTO userDTO) {
        UserEntity user = UserEntity.builder()
                                    .userId(userDTO.getUserId())
                                    .username(userDTO.getUsername())
                                    .firstName(userDTO.getFirstName())
                                    .lastName(userDTO.getLastName())
                                    .password(passwordEncoder.encode(userDTO.getPassword()))
                                    .build();
        userRepository.save(user);
    }

    public boolean isUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

}
