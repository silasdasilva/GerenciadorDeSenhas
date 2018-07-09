package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;

import com.project.silas.gerenciadordesenhas.core.abstracts.BusinessAbstract;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.FailedToWriteInternalDataException;
import com.project.silas.gerenciadordesenhas.repository.database.dao.UsuarioDao;

public class UsuarioBusiness extends BusinessAbstract {
    public UsuarioBusiness(Context context) {
        super(context);
    }

    /**
     * Método responsável por atualizar o token do usuário ativo
     * @param token
     */
    public void atualizaToken(String token) throws FailedToWriteInternalDataException {
        Usuario usuario = SessionSingletonBusiness.getUsuario();
        if(usuario == null) return;
        usuario.setTokenUsuario(token);

        UsuarioDao usuarioDao = new UsuarioDao();
        try {
            usuarioDao.update(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FailedToWriteInternalDataException();
        }
    }
}
