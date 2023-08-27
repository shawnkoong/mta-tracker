package com.nyctransittracker.mainapp.service;

import com.nyctransittracker.mainapp.dto.AuthenticationRequest;
import com.nyctransittracker.mainapp.dto.AuthenticationResponse;
import com.nyctransittracker.mainapp.dto.RegisterRequest;
import com.nyctransittracker.mainapp.dto.UserDTO;
import com.nyctransittracker.mainapp.mapper.UserDTOMapper;
import com.nyctransittracker.mainapp.model.Role;
import com.nyctransittracker.mainapp.model.User;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder encoder;
    private final UserDTOMapper userDTOMapper;
    
    public AuthenticationResponse register(RegisterRequest request) throws DuplicateKeyException {
        if (userService.checkUniqueEmail(request.getEmail())) {
            throw new DuplicateKeyException("Email already in use");
        }
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        user = userService.addUser(user);
        UserDTO userDTO = userDTOMapper.toDTO(user);
        String token = generateToken(user);
        return new AuthenticationResponse(token, "", userDTO);
    }
    
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userService.getUserWithEmail(request.getEmail());
        UserDTO userDTO = userDTOMapper.toDTO(user);
        String token = generateToken(user);
        return new AuthenticationResponse(token, "", userDTO);
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        String scope = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS))
                .subject(userDetails.getPassword())
                .claim("scope", scope)
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
