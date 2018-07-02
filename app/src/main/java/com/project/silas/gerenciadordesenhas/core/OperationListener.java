package com.project.silas.gerenciadordesenhas.core;

/**
 * Created by ansilva on 29/08/2016.
 */
public class OperationListener<T> {

    public void onSuccess(T result){};
    public void onError(Throwable error){};

    @SuppressWarnings({"UnusedParameter", "EmptyMethod"})
    public void onProgress(Integer progress){};

    public void onCancel(){};
}
