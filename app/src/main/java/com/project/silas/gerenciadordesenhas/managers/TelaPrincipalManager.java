package com.project.silas.gerenciadordesenhas.managers;

import android.content.Context;
import android.database.Cursor;

import com.project.silas.gerenciadordesenhas.business.TelaPrincipalBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

public class TelaPrincipalManager extends ManagerAbstract {

    private static final int LOADER_BUSCA_SITES_USUARIO = 200;

    private Context contexto;
    private TelaPrincipalBusiness telaPrincipalBusiness;

    public TelaPrincipalManager(Context context) {
        super(context);
        this.contexto = context;
        this.telaPrincipalBusiness = new TelaPrincipalBusiness(this.contexto);
    }

    public void buscarLogins(final String queryPesquisa, OperationListener<Cursor> operationListener) {
        runViaSyncLoader(LOADER_BUSCA_SITES_USUARIO, new OperationListener<OperationResult>(){
            @Override
            public void onSuccess(OperationResult result) {

                OperationResult<Cursor> retornoSites = telaPrincipalBusiness.buscarLogins(queryPesquisa);

                if (retornoSites.getError() != null){
                    result.withError(retornoSites.getError());
                    return;
                }
                result.withResult(retornoSites.getResult());
            }
        }, operationListener);
    }
}
