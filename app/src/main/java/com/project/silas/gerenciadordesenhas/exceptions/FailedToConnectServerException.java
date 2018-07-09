package com.project.silas.gerenciadordesenhas.exceptions;

public class FailedToConnectServerException extends Exception {
    public FailedToConnectServerException() {};

    public FailedToConnectServerException(String message) {
        super(message);
    }
}
