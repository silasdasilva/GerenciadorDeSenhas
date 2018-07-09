package com.project.silas.gerenciadordesenhas.core.abstracts;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;

public abstract class ManagerAbstract {
    protected Context context;

    public ManagerAbstract(Context context)
    {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected void runViaSyncLoader(final int loaderId, final OperationListener<OperationResult> listenerProcessador, final OperationListener listenerOuvinte) {
        final AppCompatActivity activity = (AppCompatActivity) context;
        android.content.Loader<Object> loader =  activity.getLoaderManager().getLoader(loaderId);
        if(loader != null && loader.isStarted()) {
            loader.cancelLoad();
            activity.getLoaderManager().destroyLoader(loader.getId());
        }

        activity.getLoaderManager().initLoader(loaderId, null, new LoaderManager.LoaderCallbacks<OperationResult>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public android.content.Loader<OperationResult> onCreateLoader(int i, Bundle bundle) {

                return new AsyncTaskLoader<OperationResult>(context) {
                    @Override
                    public OperationResult loadInBackground() {
                        OperationResult result = new OperationResult();
                        listenerProcessador.onSuccess(result); //cliente do método processou o result via referencia...
                        Log.e("abstract", "Executou o loader no abstract pelo menos");
                        return result; //devolvo o result processado e com valor atribuído pelo cliente
                    }
                };
            }

            @Override
            public void onLoadFinished(android.content.Loader<OperationResult> loader, OperationResult result) {
                activity.getLoaderManager().destroyLoader(loaderId);
                if(result.getError() != null) {
                    listenerOuvinte.onError(result.getError());
                    return;
                }
                listenerOuvinte.onSuccess(result.getResult());
            }

            @Override
            public void onLoaderReset(android.content.Loader<OperationResult> loader) {
                activity.getLoaderManager().destroyLoader(loaderId);
            }
        }).forceLoad();
    }
}
