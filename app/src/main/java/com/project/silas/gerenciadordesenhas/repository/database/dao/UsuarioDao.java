package com.project.silas.gerenciadordesenhas.repository.database.dao;

import android.database.sqlite.SQLiteDatabase;

import com.project.silas.gerenciadordesenhas.core.abstracts.DaoAbstract;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

public class UsuarioDao extends DaoAbstract<Usuario> {

    public UsuarioDao(SQLiteDatabase sqLiteDatabase) {
        super(Usuario.class, sqLiteDatabase);
    }

    public UsuarioDao() {
        super(Usuario.class);
    }
}
