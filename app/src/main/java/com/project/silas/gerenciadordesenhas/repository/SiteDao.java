package com.project.silas.gerenciadordesenhas.repository;

import android.database.sqlite.SQLiteDatabase;

import com.project.silas.gerenciadordesenhas.core.abstracts.DaoAbstract;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

/**
 * Created by ansilva on 22/09/2016.
 */
public class SiteDao extends DaoAbstract<Usuario> {

    public SiteDao(SQLiteDatabase sqLiteDatabase) {
        super(Usuario.class, sqLiteDatabase);
    }

    public SiteDao() {
        super(Usuario.class);
    }
}
