package com.lalit.noteapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
