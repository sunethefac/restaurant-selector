package com.govtech.restaurant_selection.security;

import com.govtech.restaurant_selection.modles.UserEntity;
import com.govtech.restaurant_selection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            return new User(userEntity.getUsername(), userEntity.getPassword(), new HashSet<>());
        } else {
            throw new UsernameNotFoundException("Invalid username or password!");
        }


    }
}
