package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.CadastroException;
import com.project.silas.gerenciadordesenhas.exceptions.FailedToConnectServerException;
import com.project.silas.gerenciadordesenhas.exceptions.LoginException;
import com.project.silas.gerenciadordesenhas.repository.database.dao.UsuarioDao;
import com.project.silas.gerenciadordesenhas.repository.network.BackendIntegrator;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class LoginUsuariosBusiness {

    protected BackendIntegrator backendIntegrator;

    private Context contexto;
    private SQLiteDatabase banco;
    private UsuarioBusiness usuarioBusiness;
    private UsuarioDao usuarioDao;

    public LoginUsuariosBusiness (Context context){
        this.contexto = context;
        this.backendIntegrator = new BackendIntegrator(this.contexto);
        this.banco = InicializacaoBusiness.getDatabase();
        this.usuarioDao = new UsuarioDao(this.banco);
    }

    public OperationResult<Usuario> efetuarLogin(Usuario usuarioLogar){
        OperationResult<Usuario> retornoLogin = new OperationResult<>();
        Cursor cursor = null;
        Usuario usuarioBanco = null;

        try{
            this.banco.beginTransaction();

            if (usuarioLogar.getEmailUsuario().equals("")) throw new LoginException("Digite um e-mail");
            if (usuarioLogar.getSenhaUsuario().equals("")) throw new LoginException("Digite uma senha");

            usuarioBanco = loginUsingDatabase(usuarioLogar);

            //Se existe internet, vou fazer o login utilizando backend para atualizar os dados contidos no dispositvo
            if(usuarioBanco != null && this.backendIntegrator.isInternetAvailable()) {
                usuarioBanco = this.loginAPI(usuarioBanco);
            }

            retornoLogin.withResult(usuarioBanco);
            this.banco.setTransactionSuccessful();

        } catch (Throwable error){
            error.printStackTrace();
            Log.i("loginBusiness", "Erro ao logar. Mensagem: " + error.getMessage());
            if (usuarioBanco != null){
                retornoLogin.withResult(usuarioBanco);
            } else {
                retornoLogin.withError(error);
            }
        } finally {
            if (cursor != null) cursor.close();
            this.banco.endTransaction();
        }
        return retornoLogin;
    }

    private Usuario loginUsingDatabase(Usuario usuarioLogar) {

        Cursor cursor = this.usuarioDao.rawQuery(Query.BUSCA_USUARIOS_CADASTRADOS, new String[]{usuarioLogar.getEmailUsuario()});

        if (cursor == null || cursor.getCount() <= 0 || cursor instanceof CursorIndexOutOfBoundsException) throw new LoginException("Este e-mail não pertence a nenhum Usuário. Tente novamente!");

        cursor.moveToFirst();
        Usuario usuarioBanco = new Usuario(cursor);
        Log.i("loginBusiness", "Confere caracteres banco:\n"
                + "\nNome: " + usuarioBanco.getNomeUsuario()
                + "\nE-mail: " + usuarioBanco.getEmailUsuario()
                + "\nSenha: " + usuarioBanco.getSenhaUsuario()
        );

        if (!usuarioBanco.getSenhaUsuario().equals(usuarioLogar.getSenhaUsuario())) throw new LoginException("Senha incorreta!");

        Log.i("loginBusiness", "Confere caracteres digitados:\n"
                + "\nNome: " + usuarioLogar.getNomeUsuario()
                + "\nE-mail: " + usuarioLogar.getEmailUsuario()
                + "\nSenha: " + usuarioLogar.getSenhaUsuario()
        );
        return usuarioBanco;
    }

    protected Usuario loginAPI(Usuario usuarioBanco) throws FailedToConnectServerException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Map<String, String> requestData = new HashMap<>();

        requestData.put("email", usuarioBanco.getEmailUsuario());
        requestData.put("password", usuarioBanco.getSenhaUsuario());

        JSONObject response = this.backendIntegrator.syncRequest(BackendIntegrator.METHOD_POST, "login", requestData, null);
        if (response == null || response.optString("type").equals("error")) throw new LoginException("Erro ao cadastrar usuário na API. Mensagem: " + response.toString());

        Log.i("loginBusiness", "JSON recebido: " + response);

        Usuario usuarioLogadoAPI = usuarioBanco;
        usuarioBanco.setTokenUsuario(response.optString(Usuario.Metadata.JSON_FIELD_TOKEN));

        Log.i("loginBusiness", "Dados usuário Antes:\n"
                + "\nId: " + usuarioBanco.getId()
                + "\nNome: " + usuarioBanco.getNomeUsuario()
                + "\nEmail: " + usuarioBanco.getEmailUsuario()
                + "\nSenha: " + usuarioBanco.getSenhaUsuario()
                + "\nToken: " + usuarioBanco.getTokenUsuario()
                + "\n\nDados usuário Depois:\n"
                + "\nId: " + usuarioLogadoAPI.getId()
                + "\nNome: " + usuarioLogadoAPI.getNomeUsuario()
                + "\nEmail: " + usuarioLogadoAPI.getEmailUsuario()
                + "\nSenha: " + usuarioLogadoAPI.getSenhaUsuario()
                + "\nToken: " + usuarioLogadoAPI.getTokenUsuario()
        );

        this.usuarioDao.updateWithOnConflict(usuarioLogadoAPI, SQLiteDatabase.CONFLICT_REPLACE);

        return usuarioLogadoAPI;
    }

    @Override
    protected void finalize() throws Throwable {
        if(this.banco != null) //database.close();();
            super.finalize();
    }

    public interface Query {
        String BUSCA_USUARIOS_CADASTRADOS = "SELECT * FROM " + Usuario.Metadata.TABLE_NAME
                + " WHERE " + Usuario.Metadata.TABLE_NAME + "." + Usuario.Metadata.FIELD_EMAIL + " = ?";
    }
}
