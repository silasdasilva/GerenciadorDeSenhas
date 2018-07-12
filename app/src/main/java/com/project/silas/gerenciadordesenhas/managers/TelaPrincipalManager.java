package com.project.silas.gerenciadordesenhas.managers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.project.silas.gerenciadordesenhas.business.FileBusiness;
import com.project.silas.gerenciadordesenhas.business.TelaPrincipalBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;
import com.project.silas.gerenciadordesenhas.entity.Site;

import java.util.Random;

public class TelaPrincipalManager extends ManagerAbstract {

    private static final int LOADER_BUSCA_SITES_USUARIO = 200;
    private static int LOADER_BUSCA_LOGO_SITE = 210;

    private Activity contexto;
    private TelaPrincipalBusiness telaPrincipalBusiness;

    public TelaPrincipalManager(Activity context) {
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
        //chamada assincrona de lista, podendo ser chamada várias vezes, por isso criado um randomico para diferenciar as chamadas
        if (this.contexto.getLoaderManager().getLoader(LOADER_BUSCA_LOGO_SITE) != null){
            LOADER_BUSCA_LOGO_SITE = new Random().nextInt(1000);
        }
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
