package com.lalit.noteapp.security;

import com.lalit.noteapp.entity.User;
import com.lalit.noteapp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class UserDetailsServiceImp implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        ()->new UsernameNotFoundException("User not found with username : "+username ));

        List<SimpleGrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map(role->new SimpleGrantedAuthority("ROLE_"+role))
                .toList();
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
