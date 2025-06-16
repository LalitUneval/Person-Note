package com.lalit.noteapp.config;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtConfig {

    @Value("${jwt.header}")
    private String header;
    @Value("${jwt.prefix}")
    private String prefix;
}
