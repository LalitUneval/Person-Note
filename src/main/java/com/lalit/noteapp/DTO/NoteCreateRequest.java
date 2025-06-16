package com.lalit.noteapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
