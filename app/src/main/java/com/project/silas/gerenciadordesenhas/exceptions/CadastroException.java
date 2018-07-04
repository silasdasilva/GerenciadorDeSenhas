package com.project.silas.gerenciadordesenhas.exceptions;

/**
 * Created by silsilva on 31/08/2017.
 */

public class CadastroException extends RuntimeException {

    public CadastroException(String mensagem){
        super(mensagem);
    }

    public CadastroException(Throwable causa) {
        super(causa);
    }

    public CadastroException(String mensagem, Throwable causa){
        super(mensagem, causa);
    }
}
