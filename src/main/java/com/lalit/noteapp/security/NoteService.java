package com.lalit.noteapp.security;

import com.lalit.noteapp.DTO.NoteCreateRequest;
import com.lalit.noteapp.DTO.NoteResponse;
import com.lalit.noteapp.DTO.NoteUpdateRequest;
import com.lalit.noteapp.entity.Note;
import com.lalit.noteapp.entity.User;
import com.lalit.noteapp.exception.AlreadyArchivedException;
import com.lalit.noteapp.exception.NotArchivedException;
import com.lalit.noteapp.exception.NoteNotFoundException;
import com.lalit.noteapp.exception.UnauthorizedAccessException;
import com.lalit.noteapp.repository.NoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

//    @Transactional
//    public NoteResponse createNote(NoteCreateRequest request , User user){
//        Note note = new Note();
//        note.setTitle(request.getTitle());
//        note.setContent(request.getContent());
//        note.setUser(user);
//        note.setCreatedAt(LocalDateTime.now());
//        note.setUpdatedAt(LocalDateTime.now());
//        Note saveNote = noteRepository.save(note);
//        return convertToNoteResponse(note);
//    }
//    @Transactional
//    public NoteResponse  getNote(Long noteId , User user){
//        Note note = noteRepository
//                .findById(noteId)
//                .orElseThrow(()->new NoteNotFoundException("Note not found"));
//        if(!note.getUser().equals(user)){
//            throw new UnauthorizedAccessException("You don't have permission to access this note");
//        }
//    return convertToNoteResponse(note);
//    }
//
//    @Transactional
//    public NoteResponse updateNote(Long noteId , NoteUpdateRequest  request , User user){
//        Note note = noteRepository
//                .findById(noteId)
//                .orElseThrow(()->new NoteNotFoundException("Note not found"));
//        if(!note.getUser().equals(user)){
//            throw new UnauthorizedAccessException("You don't have permission to access this note");
//        }
//        note.setTitle(request.getTitle());
//        note.setContent(request.getContent());
//        note.setUpdatedAt(LocalDateTime.now());
//        Note updated = noteRepository.save(note);
//        return convertToNoteResponse(note);
//    }
//
//    @Transactional
//    public void archiveNote(Long noteId , User user){
//        Note note = noteRepository
//                .findById(noteId)
//                .orElseThrow(()->new NoteNotFoundException("Note not found"));
//        if(!note.getUser().equals(user)){
//            throw new UnauthorizedAccessException("You don't have permission to access this note");
//        }
//        note.setArchived(true);
//        noteRepository.save(note);
//    }
//public List<NoteResponse>  getUserNotes(User user , boolean includeArchived) {
//      // if includeArchived= true then it will return all the note(simple note + archived notes)
//    //if includeArchived = false  then it will return the non atchived notes
//    List<Note> notes = includeArchived ?
//            noteRepository.findByUser(user) :
//            noteRepository.findByUserAndIsArchivedFalse(user);
//    return notes.stream()
//            .map(note -> convertToNoteResponse(note)).toList();
//}
//
//
//
//
//
//
//    private NoteResponse convertToNoteResponse(Note note) {
//        return NoteResponse.builder()
//                .id(note.getId())
//                .title(note.getTitle())
//                .content(note.getContent())
//                .userId(note.getUser().getId())
//                .updatedAt(note.getUpdatedAt())
//                .createdAt(note.getCreatedAt())
//                .isArchieved(note.isArchived())
//                .build();
//
//    }
@Transactional
public NoteResponse createNote(NoteCreateRequest request, User user) {
    Note note = new Note();
    note.setTitle(request.getTitle());
    note.setContent(request.getContent());
    note.setUser(user);
    note.setCreatedAt(LocalDateTime.now());
    note.setUpdatedAt(LocalDateTime.now());
    note.setArchived(false);

    Note savedNote = noteRepository.save(note);
    return convertToNoteResponse(savedNote);
}

    @Transactional
    public NoteResponse getNote(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!note.getUser().equals(user)) {
            throw new UnauthorizedAccessException("You don't have permission to access this note");
        }

        return convertToNoteResponse(note);
    }

    @Transactional
    public NoteResponse updateNote(Long noteId, NoteUpdateRequest request, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!note.getUser().equals(user)) {
            throw new UnauthorizedAccessException("You don't have permission to edit this note");
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUpdatedAt(LocalDateTime.now());

        Note updatedNote = noteRepository.save(note);
        return convertToNoteResponse(updatedNote);
    }

    @Transactional
    public void archiveNote(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!note.getUser().equals(user)) {
            throw new UnauthorizedAccessException("You don't have permission to archive this note");
        }

        if (note.isArchived()) {
            throw new AlreadyArchivedException("Note is already archived");
        }

        note.setArchived(true);
        noteRepository.save(note);
    }

    @Transactional
    public void unarchiveNote(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        if (!note.getUser().equals(user)) {
            throw new UnauthorizedAccessException("You don't have permission to unarchive this note");
        }

        if (!note.isArchived()) {
            throw new NotArchivedException("Note is not archived");
        }

        note.setArchived(false);
        noteRepository.save(note);
    }

    public List<NoteResponse> getUserNotes(User user, boolean includeArchived) {
        List<Note> notes = includeArchived ?
                noteRepository.findByUser(user) :
                noteRepository.findByUserAndIsArchivedFalse(user);
        return notes.stream()
                .map(this::convertToNoteResponse)
                .toList();
    }

    public List<NoteResponse> getArchivedNotes(User user) {
        List<Note> notes = noteRepository.findByUserAndIsArchivedTrue(user);
        return notes.stream()
                .map(this::convertToNoteResponse)
                .toList();
    }

    public List<NoteResponse> getNonArchivedNotes(User user) {
        List<Note> notes = noteRepository.findByUserAndIsArchivedFalse(user);
        return notes.stream()
                .map(this::convertToNoteResponse)
                .toList();
    }

    private NoteResponse convertToNoteResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .userId(note.getUser().getId())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .isArchived(note.isArchived())
                .build();
    }
}
