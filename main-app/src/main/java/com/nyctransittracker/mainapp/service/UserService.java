package com.nyctransittracker.mainapp.service;

import com.nyctransittracker.mainapp.model.User;
import com.nyctransittracker.mainapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUser(int id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
