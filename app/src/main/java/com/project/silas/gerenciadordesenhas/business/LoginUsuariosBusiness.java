package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.FailedToConnectServerException;
import com.project.silas.gerenciadordesenhas.exceptions.LoginException;
import com.project.silas.gerenciadordesenhas.repository.database.dao.UsuarioDao;
import com.project.silas.gerenciadordesenhas.repository.network.BackendIntegrator;

import org.json.JSONObject;

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

        try{

            if (usuarioLogar.getEmailUsuario().equals("")) throw new LoginException("Digite um e-mail");
            if (usuarioLogar.getSenhaUsuario().equals("")) throw new LoginException("Digite uma senha");

            Log.i("loginBusiness", "Confere caracteres digitados:\n"
                    + "\nId: " + usuarioLogar.getId()
                    + "\nNome: " + usuarioLogar.getNomeUsuario()
                    + "\nE-mail: " + usuarioLogar.getEmailUsuario()
                    + "\nSenha: " + usuarioLogar.getSenhaUsuario()
            );

            //Se existe internet, vou fazer o login utilizando backend
            if(this.backendIntegrator.isInternetAvailable()) {
                OperationResult<Usuario> result = this.loginUsingNetwork(usuarioLogar.getEmailUsuario(), usuarioLogar.getSenhaUsuario());
                if(result.getResult() == null) return result;

                SessionSingletonBusiness.setUsuario(result.getResult());
                return result;
            }
            //Caso contrário realizo o login utilizando o banco de dados local:
            OperationResult<Usuario> result = this.loginUsingDatabase(usuarioLogar.getEmailUsuario(), usuarioLogar.getSenhaUsuario());
            SessionSingletonBusiness.setUsuario(result.getResult());

        } catch (Throwable error){
            error.printStackTrace();
            Log.i("loginBusiness", "Erro ao logar. Mensagem: " + error.getMessage());
            retornoLogin.withError(error);
        } finally {
            if (cursor != null) cursor.close();
        }
        return retornoLogin;
    }

    @Override
    protected void finalize() throws Throwable {
        if(this.banco != null) //database.close();();
            super.finalize();
    }

    private OperationResult<Usuario> loginUsingDatabase(String login, String password) {
        Cursor cursor = this.banco.rawQuery(Query.BUSCA_USUARIOS_CADASTRADOS, new String[]{login});

        if (cursor == null || cursor.getCount() <= 0 || cursor instanceof CursorIndexOutOfBoundsException) throw new LoginException("Este e-mail não pertence a nenhum Usuário. Tente novamente!");

        cursor.moveToFirst();
        Usuario usuario = new Usuario(cursor);
        Log.i("loginBusiness", "Confere caracteres banco:\n"
                + "\nId: " + usuario.getId()
                + "\nNome: " + usuario.getNomeUsuario()
                + "\nE-mail: " + usuario.getEmailUsuario()
                + "\nSenha: " + usuario.getSenhaUsuario()
        );
        if (!usuario.getSenhaUsuario().equals(password)) throw new LoginException("Senha incorreta!");
        return new OperationResult<Usuario>().withResult(usuario);
    }

    protected OperationResult<Usuario> loginUsingNetwork(String login, String password) throws FailedToConnectServerException {
        Map<String, String> requestData = new HashMap<String, String>();
        OperationResult<Usuario> resultado = new OperationResult<>();
        Usuario usuario;

        requestData.put("name", login);
        requestData.put("email", login);
        requestData.put("password", password);
        JSONObject response = this.backendIntegrator.syncRequest(BackendIntegrator.METHOD_POST, "login", requestData);
        if (response == null || response.optString("type").equals("error")) throw new LoginException("Erro ao cadastrar usuário na API. Mensagem: " + response.toString());

        Log.i("loginBusiness", "JSON recebido: " + response);
        //JSONObject jsonPessoa = response.optJSONObject("pessoa");
        usuario = new Usuario(response);

        try {
            this.banco.beginTransaction();
            this.usuarioDao.insertWithOnConflict(usuario, SQLiteDatabase.CONFLICT_REPLACE);
            this.banco.setTransactionSuccessful();

            SessionSingletonBusiness.setUsuario(usuario);

            resultado.withResult(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            resultado.withError(e);
            throw new FailedToConnectServerException();
        } finally {
            if(this.banco.inTransaction()) {
                this.banco.endTransaction();
            }
        }
        return resultado;
    }

    public interface Query {
        String BUSCA_USUARIOS_CADASTRADOS = "SELECT * FROM " + Usuario.Metadata.TABLE_NAME
                + " WHERE " + Usuario.Metadata.TABLE_NAME + "." + Usuario.Metadata.FIELD_EMAIL + " = ?";
    }
}
