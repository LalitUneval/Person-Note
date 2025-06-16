package com.lalit.noteapp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
// signup is used for creating the user
// signin is used for logging the user
public class SignupRequest {

    @NotBlank
    @Size(min = 3 , max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(min = 6,max = 40)
    private String password;

    private Set<String >roles;
}
