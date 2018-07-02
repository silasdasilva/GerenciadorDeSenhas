package com.project.silas.gerenciadordesenhas.core;

/**
 * Created by ansilva on 29/08/2016.
 */
public class OperationResult<T> {
    private T result = null;
    private Throwable error = null;


    public T getResult() {
        return result;
    }

    public OperationResult<T> withResult(T result) {
        this.result = result;
        return this;
    }

    public Throwable getError() {
        return error;
    }

    public OperationResult<T> withError(Throwable error) {
        this.error = error;
        return this;
    }
}
