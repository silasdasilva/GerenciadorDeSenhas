package com.project.silas.gerenciadordesenhas.exceptions;

/**
 * Created by silsilva on 31/08/2017.
 */

public class LoginException extends RuntimeException {

    public LoginException(String mensagem){
        super(mensagem);
    }

    public LoginException(Throwable causa) {
        super(causa);
    }

    public LoginException(String mensagem, Throwable causa){
        super(mensagem, causa);
    }
}
