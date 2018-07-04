package com.project.silas.gerenciadordesenhas.managers;

import android.content.Context;

import com.project.silas.gerenciadordesenhas.business.CadastroUsuariosBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

public class CadastroUsuariosManager extends ManagerAbstract {

    private static final int LOADER_CADASTRO_USUARIOS = 100;

    private final Context contexto;
    private CadastroUsuariosBusiness cadastroUsuariosBusiness;

    public CadastroUsuariosManager(Context context) {
        super(context);
        this.contexto = context;
    }

    public void cadastrarUsuario (final Usuario usuarioCadastro, final OperationListener<Usuario> listenerCadastro){
        runViaSyncLoader(LOADER_CADASTRO_USUARIOS, new OperationListener<OperationResult>(){
            @Override
            public void onSuccess(OperationResult result) {
                OperationResult<Usuario> retornoCadastro = cadastroUsuariosBusiness.cadastrarUsuario(usuarioCadastro);

                if (retornoCadastro.getError() != null){
                    result.withError(retornoCadastro.getError());
                    return;
                }
                result.withResult(retornoCadastro.getResult());
            }
        }, listenerCadastro);
    }
}
