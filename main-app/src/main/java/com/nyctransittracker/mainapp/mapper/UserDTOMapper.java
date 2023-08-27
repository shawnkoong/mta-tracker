package com.nyctransittracker.mainapp.mapper;

import com.nyctransittracker.mainapp.dto.UserDTO;
import com.nyctransittracker.mainapp.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserDTOMapper {

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getSubscribedRoutes()
        );
    }
}
