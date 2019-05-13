package com.github.senyast4745.db;

public class DuplicateDataException extends RuntimeException {
    DuplicateDataException(String s) {
        super(s);
    }
}
