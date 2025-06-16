package com.lalit.noteapp.controller;

import com.lalit.noteapp.DTO.JwtResponse;
import com.lalit.noteapp.DTO.LoginRequest;
import com.lalit.noteapp.DTO.MessageResponse;
import com.lalit.noteapp.DTO.SignupRequest;
import com.lalit.noteapp.entity.User;
import com.lalit.noteapp.repository.UserRepository;
import com.lalit.noteapp.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils utils;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, JwtUtils utils, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.utils = utils;
        this.encoder = encoder;
    }

    @PostMapping("/signin")//login request
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = utils.generateJwtToken(authentication);
            User user = userRepository
                    .findByUsername(request.getUsername())
                    .orElseThrow(()->new UsernameNotFoundException("User not found with usrename : "+request.getUsername()));
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRoles()));

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
    @PostMapping("/signup")//register request
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request){
       try{
           if(userRepository.existsByUsername(request.getUsername())){
               return ResponseEntity
                       .badRequest().body(new MessageResponse("Error: Username is already taken!"));
           }
           if(userRepository.existsByEmail(request.getEmail())){
               return ResponseEntity
                       .badRequest()
                       .body(new MessageResponse("Error: Email is already taken!"));
           }
           User user = new User();
           user.setEmail(request.getEmail());
           user.setUsername(request.getUsername());
           user.setPassword(encoder.encode(request.getPassword()));
           if(request.getRoles()==null || request.getRoles().isEmpty()){
               user.setRoles(Collections.singleton("ROLE_USER"));
           }else {
               user.setRoles(new HashSet<>(request.getRoles()));
           }
           userRepository.save(user);
           return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
       } catch (Exception e) {
           return ResponseEntity.internalServerError()
                   .body(new MessageResponse("Error: " + e.getMessage()));
       }
    }
}
