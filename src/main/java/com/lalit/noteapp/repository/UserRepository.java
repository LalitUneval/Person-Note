package com.lalit.noteapp.repository;

import com.lalit.noteapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

    Optional<User> findByUsername(String name);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String name);

    Boolean existsByEmail(String name);
}
