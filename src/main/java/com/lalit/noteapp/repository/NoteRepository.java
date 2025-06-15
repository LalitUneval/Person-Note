package com.lalit.noteapp.repository;

import com.lalit.noteapp.entity.Note;
import com.lalit.noteapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note , Long> {


    List<Note> findByUserAndIsArchivedFalse(User user);
    List<Note> findByUserAndIsArchivedTrue(User user);

    List<Note> findByUser(User user);
}
