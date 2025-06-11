package com.seb.musicapp.common;

import java.io.IOException;

public class WrongCodeException extends IOException {
    public WrongCodeException(String message) {
        super(message);
    }
}
