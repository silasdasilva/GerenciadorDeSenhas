package com.project.silas.gerenciadordesenhas.managers;

import android.content.Context;

import com.project.silas.gerenciadordesenhas.business.LoginUsuariosBusiness;
import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.ManagerAbstract;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

public class LoginUsuariosManager extends ManagerAbstract {

    private Context contexto;
    private LoginUsuariosBusiness loginUsuariosBusiness;

    public LoginUsuariosManager(Context context) {
        super(context);
        this.contexto = context;
    }

    public void efetuarLogin(Usuario usuarioLogar, OperationListener<Usuario> listenerLogin) {
        OperationResult<Usuario> retornoLogin = this.loginUsuariosBusiness.efetuarLogin(usuarioLogar);

        if (retornoLogin.getError() != null){
            listenerLogin.onError(retornoLogin.getError());
            return;
        }
        listenerLogin.onSuccess(retornoLogin.getResult());
    }
}
