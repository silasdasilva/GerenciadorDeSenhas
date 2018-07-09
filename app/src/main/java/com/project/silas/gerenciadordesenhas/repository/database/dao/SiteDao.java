package com.project.silas.gerenciadordesenhas.repository.database.dao;

import android.database.sqlite.SQLiteDatabase;

import com.project.silas.gerenciadordesenhas.core.abstracts.DaoAbstract;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

public class SiteDao extends DaoAbstract<Site> {

    public SiteDao(SQLiteDatabase sqLiteDatabase) {
        super(Site.class, sqLiteDatabase);
    }

    public SiteDao() {
        super(Site.class);
    }
}
