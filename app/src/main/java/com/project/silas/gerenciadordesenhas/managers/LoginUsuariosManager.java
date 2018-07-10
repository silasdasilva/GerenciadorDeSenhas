package com.project.silas.gerenciadordesenhas.managers;

import android.content.Context;

import com.project.silas.gerenciadordesenhas.business.LoginUsuariosBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

public class LoginUsuariosManager extends ManagerAbstract {

    private static final int LOADER_LOGIN_USUARIO = 0;

    private Context contexto;
    private LoginUsuariosBusiness loginUsuariosBusiness;

    public LoginUsuariosManager(Context context) {
        super(context);
        this.contexto = context;
        this.loginUsuariosBusiness = new LoginUsuariosBusiness(this.contexto);
    }

    public void efetuarLogin(final Usuario usuarioLogar, OperationListener<Usuario> listenerLogin) {
        runViaSyncLoader(LOADER_LOGIN_USUARIO, new OperationListener<OperationResult>(){
            @Override
            public void onSuccess(OperationResult result) {

                OperationResult<Usuario> retornoLogin = loginUsuariosBusiness.efetuarLogin(usuarioLogar);

                if (retornoLogin.getError() != null){
                    result.withError(retornoLogin.getError());
                    return;
                }
                result.withResult(retornoLogin.getResult());

            }
        }, listenerLogin);
    }
}
