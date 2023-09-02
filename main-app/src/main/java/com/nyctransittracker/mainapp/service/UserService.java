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

    public boolean checkUniqueEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User getUserWithEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    public void addSubscription(User user, String routeId) {
        user.getSubscribedRoutes().add(routeId);
        userRepository.save(user);
    }

    public void removeSubscription(User user, String routeId) {
        user.getSubscribedRoutes().remove(routeId);
        userRepository.save(user);
    }
}
