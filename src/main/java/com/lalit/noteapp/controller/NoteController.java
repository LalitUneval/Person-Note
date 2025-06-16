package com.lalit.noteapp.controller;

import com.lalit.noteapp.DTO.MessageResponse;
import com.lalit.noteapp.DTO.NoteCreateRequest;
import com.lalit.noteapp.DTO.NoteResponse;
import com.lalit.noteapp.DTO.NoteUpdateRequest;
import com.lalit.noteapp.entity.User;
import com.lalit.noteapp.repository.UserRepository;
import com.lalit.noteapp.security.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final UserRepository userRepository;
    private final NoteService noteService;

    public NoteController(UserRepository userRepository, NoteService noteService) {
        this.userRepository = userRepository;
        this.noteService = noteService;
    }

private User getCurrentUser(Authentication authentication) {
    String username = authentication.getName();
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
}

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @RequestBody NoteCreateRequest request,
            Authentication authentication) {
        NoteResponse response = noteService.createNote(request, getCurrentUser(authentication));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNote(
            @PathVariable Long id,
            Authentication authentication) {
        NoteResponse response = noteService.getNote(id, getCurrentUser(authentication));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long id,
            @RequestBody NoteUpdateRequest request,
            Authentication authentication) {
        NoteResponse response = noteService.updateNote(id, request, getCurrentUser(authentication));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<MessageResponse> archiveNote(
            @PathVariable Long id,
            Authentication authentication) {
        noteService.archiveNote(id, getCurrentUser(authentication));
        return ResponseEntity.ok(new MessageResponse("Note archived successfully"));
    }

    @PutMapping("/{id}/unarchive")
    public ResponseEntity<MessageResponse> unarchiveNote(
            @PathVariable Long id,
            Authentication authentication) {
        noteService.unarchiveNote(id, getCurrentUser(authentication));
        return ResponseEntity.ok(new MessageResponse("Note unarchived successfully"));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getNotes(
            @RequestParam(required = false, defaultValue = "false") boolean includeArchived,
            Authentication authentication) {
        List<NoteResponse> notes = noteService.getUserNotes(getCurrentUser(authentication), includeArchived);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/archived")
    public ResponseEntity<List<NoteResponse>> getArchivedNotes(Authentication authentication) {
        List<NoteResponse> notes = noteService.getArchivedNotes(getCurrentUser(authentication));
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/active")
    public ResponseEntity<List<NoteResponse>> getActiveNotes(Authentication authentication) {
        List<NoteResponse> notes = noteService.getNonArchivedNotes(getCurrentUser(authentication));
        return ResponseEntity.ok(notes);
    }
}
