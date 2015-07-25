package ru.iostd.safebox.exceptions;

public class BadMasterKeyException extends Exception {

    public BadMasterKeyException() {
    }

    public BadMasterKeyException(String msg) {
        super(msg);
    }
}
