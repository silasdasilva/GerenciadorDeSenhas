package com.project.silas.gerenciadordesenhas.managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.project.silas.gerenciadordesenhas.business.InicializacaoBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;

public class InicializacaoManager extends ManagerAbstract {

    private static final int LOADER_INICIALIZACAO = 1;

    private Context contexto;
    private InicializacaoBusiness inicializacaoBusiness;

    public InicializacaoManager(@NonNull Context context) {
        super(context);
        this.contexto = context;
        this.inicializacaoBusiness = new InicializacaoBusiness(this.contexto);
    }

    public void inializarDados(final OperationListener<Boolean> operationListener) {
        runViaSyncLoader(LOADER_INICIALIZACAO, new OperationListener<OperationResult>() {
            @Override
            public void onSuccess(OperationResult result) {
                OperationResult<Boolean> retornoInicio = inicializacaoBusiness.criacaoBanco();

                if (retornoInicio.getError() != null){
                    operationListener.onError(retornoInicio.getError());
                    return;
                }
                operationListener.onSuccess(retornoInicio.getResult());
            }
        }, operationListener);
    }
}
