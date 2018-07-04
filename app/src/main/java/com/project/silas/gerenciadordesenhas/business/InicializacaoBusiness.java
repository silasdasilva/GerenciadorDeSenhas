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
import com.project.silas.gerenciadordesenhas.entity.Usuario;

public class InicializacaoBusiness {

    public static final String NOME_BANCO_DE_DADOS = "GerenciadorSenhas.db";
    private static SQLiteOpenHelper sqLiteOpenHelper;

    private Context contexto;

    public InicializacaoBusiness(Context context){
        this.contexto = context;
    }

    public OperationResult<Void> criacaoBanco(){
        OperationResult<Void> retornoInicio = new OperationResult<>();

        try {
            if (sqLiteOpenHelper == null) {
                sqLiteOpenHelper = new SQLiteOpenHelper(this.contexto, NOME_BANCO_DE_DADOS, null, 1) {
                    @Override
                    public void onCreate(SQLiteDatabase sqLiteDatabase) {
                        criacaoTabelasIniciais(sqLiteDatabase);
                    }

                    @Override
                    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                        atualizacaoTabelas(oldVersion, newVersion);
                    }
                };
            }
            retornoInicio.withResult(null);

        } catch (Throwable error){
            error.printStackTrace();
            retornoInicio.withError(error);
            Log.i("inicialBusiness", "Erro ao inicializar. Mensagem: " + error.getMessage());
        }
        return retornoInicio;
    }

    public static synchronized SQLiteDatabase getDatabase() {
        return sqLiteOpenHelper.getWritableDatabase();
    }

    public static synchronized SQLiteDatabase getReadOnlyDatabase() {
        return sqLiteOpenHelper.getReadableDatabase();
    }

    private void criacaoTabelasIniciais(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nomeUsuario VARCHAR(100), emailUsuario VARCHAR(100), senhaUsuario VARCHAR(50), tokenUsuario VARCHAR(100))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Sites (id INTEGER PRIMARY KEY AUTOINCREMENT, urlSalva VARCHAR(200), idUsuario VARCHAR(100), emailSalvo VARCHAR(100), senhaSalva VARCHAR(50))");
    }

    private void atualizacaoTabelas(int ultimaVersao, int novaVersao){
        //lógica de atualização quando houver
        if (ultimaVersao < novaVersao){

        }
    }

    public OperationResult<Integer> buscaTotalUsuarios (){
        OperationResult<Integer> retornoUsuarios = new OperationResult<>();
        Cursor cursor = null;
        int totalUsuarios = 0;
        try {
            cursor = getDatabase().rawQuery(Query.GET_TOTAL_USUARIOS, null);

            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                totalUsuarios = cursor.getInt(cursor.getColumnIndex("total"));
                Log.i("inicialBusiness", "Total retornado do banco: " + totalUsuarios);
            }

            retornoUsuarios.withResult(totalUsuarios);
        } catch (Throwable error){
            error.printStackTrace();
            Log.i("inicialBusiness", "Erro ao buscar usuários. Mensagem: " + error.getMessage());
            retornoUsuarios.withError(error);
        } finally {
            if (cursor != null) cursor.close();
        }
        return retornoUsuarios;
    }

    public interface Query {

        String GET_TOTAL_USUARIOS = "SELECT COUNT(*) AS total FROM " + Usuario.Metadata.TABLE_NAME
                + " WHERE " + Usuario.Metadata.TABLE_NAME + "." + Usuario.Metadata.FIELD_SENHA + " != 'null'";
    }
}
