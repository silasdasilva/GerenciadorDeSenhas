package com.project.silas.gerenciadordesenhas.managers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.project.silas.gerenciadordesenhas.business.FileBusiness;
import com.project.silas.gerenciadordesenhas.business.TelaPrincipalBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;
import com.project.silas.gerenciadordesenhas.entity.Site;

public class TelaPrincipalManager extends ManagerAbstract {

    private static final int LOADER_BUSCA_SITES_USUARIO = 200;
    private static final int LOADER_BUSCA_LOGO_SITE = 210;

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

    public void buscarLogoSite(final Site site, OperationListener<Bitmap> listenerLogo) {
        runViaSyncLoader(LOADER_BUSCA_LOGO_SITE, new OperationListener<OperationResult>(){
            @Override
            public void onSuccess(OperationResult result) {
                OperationResult<Bitmap> retornoLogo = telaPrincipalBusiness.buscaLogoSite(site);

                if (retornoLogo.getError() != null){
                    result.withError(retornoLogo.getError());
                    return;
                }
                result.withResult(retornoLogo.getResult());
            }
        }, listenerLogo);
    }
}
