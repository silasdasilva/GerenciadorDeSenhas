package com.project.silas.gerenciadordesenhas.managers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.project.silas.gerenciadordesenhas.business.CadastroSiteBusiness;
import com.project.silas.gerenciadordesenhas.business.FileBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;
import com.project.silas.gerenciadordesenhas.entity.Site;

import java.util.Random;

public class CadastroSiteManager extends ManagerAbstract{

    private static final int LOADER_INSERCAO_SITE = 200;
    private static final int LOADER_ATUALIZACAO_SITE = 300;
    private static final int LOADER_EXCLUSAO_SITE = 400;
    private static int LOADER_BUSCA_LOGO_SITE = 500;

    private Activity contexto;
    private CadastroSiteBusiness cadastroSiteBusiness;

    public CadastroSiteManager(Activity context) {
        super(context);
        this.contexto = context;
        this.cadastroSiteBusiness = new CadastroSiteBusiness(this.contexto);
    }

    public void insereSite(final Site siteModificacao, OperationListener<Site> operationListener) {
        runViaSyncLoader(LOADER_INSERCAO_SITE, new OperationListener<OperationResult>(){
            @Override
            public void onSuccess(OperationResult result) {
                OperationResult<Site> retornoInsercao = cadastroSiteBusiness.insereSite(siteModificacao);

                if (retornoInsercao.getError() != null){
                    result.withError(retornoInsercao.getError());
                    return;
                }
                result.withResult(retornoInsercao.getResult());
            }
        }, operationListener);
    }

    public void atualizaSite(final Site siteModificacao, OperationListener<Site> operationListener) {
        runViaSyncLoader(LOADER_ATUALIZACAO_SITE, new OperationListener<OperationResult>(){
            @Override
            public void onSuccess(OperationResult result) {
                OperationResult<Site> retornoAtualizacao = cadastroSiteBusiness.atualizaSite(siteModificacao);

                if (retornoAtualizacao.getError() != null){
                    result.withError(retornoAtualizacao.getError());
                    return;
                }
                result.withResult(retornoAtualizacao.getResult());
            }
        }, operationListener);
    }

    public void excluiSite(final Site siteModificacao, OperationListener<Site> operationListener) {
        runViaSyncLoader(LOADER_EXCLUSAO_SITE, new OperationListener<OperationResult>(){
            @Override
            public void onSuccess(OperationResult result) {
                OperationResult<Site> retornoExclusao = cadastroSiteBusiness.excluiSite(siteModificacao);

                if (retornoExclusao.getError() != null){
                    result.withError(retornoExclusao.getError());
                    return;
                }
                result.withResult(retornoExclusao.getResult());
            }
        }, operationListener);
    }

    public void buscarLogoSite(final Site site, OperationListener<Bitmap> listenerLogo) {
        if (this.contexto.getLoaderManager().getLoader(LOADER_BUSCA_LOGO_SITE) != null){
            LOADER_BUSCA_LOGO_SITE = new Random().nextInt(1000);
        }
        runViaSyncLoader(LOADER_BUSCA_LOGO_SITE, new OperationListener<OperationResult>(){
            @Override
            public void onSuccess(OperationResult result) {
                OperationResult<Bitmap> retornoLogo = cadastroSiteBusiness.buscaLogoDisco(site);

                if (retornoLogo.getError() != null){
                    result.withError(retornoLogo.getError());
                    return;
                }
                result.withResult(retornoLogo.getResult());
            }
        }, listenerLogo);
    }
}
