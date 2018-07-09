package com.project.silas.gerenciadordesenhas.core.abstracts;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONObject;

public abstract class BusinessAbstract {
    protected Context context;

    public BusinessAbstract(Context context)
    {
        this.context = context;
    }

    public JSONObject pacoteDeEnvio(Cursor cursor) {
        JSONObject ipr = new JSONObject();
        return null;
    }
}
