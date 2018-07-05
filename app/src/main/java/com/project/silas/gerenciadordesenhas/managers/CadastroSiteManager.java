package com.project.silas.gerenciadordesenhas.managers;

import android.content.Context;

import com.project.silas.gerenciadordesenhas.business.CadastroSiteBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;
import com.project.silas.gerenciadordesenhas.entity.Site;

public class CadastroSiteManager extends ManagerAbstract{

    private static final int LOADER_INSERCAO_SITE = 200;
    private static final int LOADER_ATUALIZACAO_SITE = 300;
    private static final int LOADER_EXCLUSAO_SITE = 400;

    private Context contexto;
    private CadastroSiteBusiness cadastroSiteBusiness;

    public CadastroSiteManager(Context context) {
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
        runViaSyncLoader(LOADER_INSERCAO_SITE, new OperationListener<OperationResult>(){
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
        runViaSyncLoader(LOADER_INSERCAO_SITE, new OperationListener<OperationResult>(){
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
}
