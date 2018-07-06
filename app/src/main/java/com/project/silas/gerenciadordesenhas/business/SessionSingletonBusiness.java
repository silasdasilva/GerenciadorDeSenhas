package com.project.silas.gerenciadordesenhas.business;

import com.project.silas.gerenciadordesenhas.entity.Usuario;

/**
 * Created by silas on 05/07/2019.
 */
public class SessionSingletonBusiness {
    private static Usuario usuario;

    public synchronized static Usuario getUsuario() {
        return usuario;
    }

    public synchronized static void setUsuario(Usuario usuario) {
        SessionSingletonBusiness.usuario = usuario;
    }
}
