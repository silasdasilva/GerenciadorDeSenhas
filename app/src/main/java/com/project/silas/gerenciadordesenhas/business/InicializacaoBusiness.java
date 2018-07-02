package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Path;
import android.support.v4.content.Loader;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationListener;
import com.project.silas.gerenciadordesenhas.core.OperationResult;

public class InicializacaoBusiness {

    public static final String NOME_BANCO_DE_DADOS = "GerenciadorSenhas.db";
    private static SQLiteOpenHelper sqLiteOpenHelper;

    private Context contexto;

    public InicializacaoBusiness(Context context){
        this.contexto = context;
    }

    public OperationResult<Boolean> criacaoBanco(){
        OperationResult<Boolean> retornoInicio = new OperationResult<>();

        try {
            if (sqLiteOpenHelper == null) {
                sqLiteOpenHelper = new SQLiteOpenHelper(this.contexto, NOME_BANCO_DE_DADOS, null, 1) {
                    @Override
                    public void onCreate(SQLiteDatabase sqLiteDatabase) {
                        criacaoTabelasIniciais();
                    }

                    @Override
                    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                        atualizacaoTabelas(oldVersion, newVersion);
                    }
                };
                retornoInicio.withResult(true);
            } else {
                retornoInicio.withResult(false);
            }

        } catch (Throwable error){
            error.printStackTrace();
            retornoInicio.withError(error);
            Log.i("inicialBusiness", "Erro ao inicializar. Mensagem: " + error.getMessage());
        } finally {
            if (getDatabase().inTransaction()) getDatabase().endTransaction();
        }
        return retornoInicio;
    }

    public static synchronized SQLiteDatabase getDatabase() {
        return sqLiteOpenHelper.getWritableDatabase();
    }

    public static synchronized SQLiteDatabase getReadOnlyDatabase() {
        return sqLiteOpenHelper.getReadableDatabase();
    }

    private void criacaoTabelasIniciais() {
        getDatabase().beginTransaction();
        getDatabase().execSQL("CREATE TABLE IF NOT EXISTS Usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nome VARCHAR(50), email VARCHAR(100), senha VARCHAR(50))");
        getDatabase().execSQL("CREATE TABLE IF NOT EXISTS Logins (id INTEGER PRIMARY KEY AUTOINCREMENT, url VARCHAR(200), usuario VARCHAR(100), senha VARCHAR(50))");
        getDatabase().setTransactionSuccessful();
    }

    private void atualizacaoTabelas(int ultimaVersao, int novaVersao){
        //lógica de atualização quando houver
    }
}
