package com.project.silas.gerenciadordesenhas.managers;

import android.content.Context;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.project.silas.gerenciadordesenhas.business.InicializacaoBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;

public class InicializacaoManager extends ManagerAbstract {

    private static final int LOADER_BUSCA_USUARIOS = 1;

    private Context contexto;
    private InicializacaoBusiness inicializacaoBusiness;

    public InicializacaoManager(@NonNull Context context) {
        super(context);
        this.contexto = context;
        this.inicializacaoBusiness = new InicializacaoBusiness(this.contexto);
    }

    public OperationListener<Void> inializarDados(final OperationListener<Void> operationListener) {
        OperationResult<Void> retornoInicio = inicializacaoBusiness.criacaoBanco();

        if (retornoInicio.getError() != null){
            operationListener.onError(retornoInicio.getError());
        } else {
            operationListener.onSuccess(retornoInicio.getResult());
        }
        return operationListener;

    }

    public OperationListener<Integer> buscaTotalUsuarios(final OperationListener<Integer> operationListener) {
        OperationResult<Integer> retornoInicio = inicializacaoBusiness.buscaTotalUsuarios();

        if (retornoInicio.getError() != null){
            operationListener.onError(retornoInicio.getError());
        } else {
            operationListener.onSuccess(retornoInicio.getResult());
        }
        return operationListener;
    }
}
