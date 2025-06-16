package com.lalit.noteapp.exception;

public class AlreadyArchivedException extends RuntimeException {
    public AlreadyArchivedException(String message) {
        super(message);
    }
}