package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.CadastroException;
import com.project.silas.gerenciadordesenhas.repository.database.dao.UsuarioDao;
import com.project.silas.gerenciadordesenhas.repository.network.BackendIntegrator;

import org.json.JSONObject;

import java.security.KeyStore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CadastroUsuariosBusiness {

    private Context contexto;
    private SQLiteDatabase bancoDeDados;
    private UsuarioDao usuarioDao;
    protected BackendIntegrator backendIntegrator;

    public CadastroUsuariosBusiness (Context context){
        this.contexto = context;
        this.bancoDeDados = InicializacaoBusiness.getDatabase();
        this.backendIntegrator = new BackendIntegrator(this.contexto);
        this.usuarioDao = new UsuarioDao(this.bancoDeDados);
    }

    public OperationResult<Usuario> cadastrarUsuario(Usuario usuarioCadastro) {
        Map<String, String> requestData = new HashMap<String, String>();
        OperationResult<Usuario> retornoCadastro = new OperationResult<>();
        Cursor cursor = null;

        try {
            this.bancoDeDados.beginTransaction();

            //Verificação campos vazios
            if (usuarioCadastro.getNomeUsuario().equals("")) throw new CadastroException("Preencha seu nome!");
            if (usuarioCadastro.getEmailUsuario().equals("")) throw new CadastroException("Preencha seu e-mail!");
            if (usuarioCadastro.getSenhaUsuario().equals("")) throw new CadastroException("Preencha sua senha!");


            Log.i("cadastroBusiness", "Confere caracteres digitados:\n"
                    + "\nNome: " + usuarioCadastro.getNomeUsuario()
                    + "\nE-mail: " + usuarioCadastro.getEmailUsuario()
                    + "\nSenha: " + usuarioCadastro.getSenhaUsuario()
            );

            cursor = this.usuarioDao.rawQuery(Query.CONFERE_EXISTENCIA_EMAIL, new String[]{usuarioCadastro.getEmailUsuario()});

            //Verificar se alguém já está utilizando o e-mail
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex("verificacaoEmail")) > 0) throw new CadastroException("Já existe um Usuário com esse email cadastrado");

            /**
             * Verificação da senha
             *
             * Mínimo de 8 caracteres
             * Mínimo de 1 letra
             * Mínimo de 1 número
             * Mínimo de 1 caractere especial
             *
             * */

            if (usuarioCadastro.getSenhaUsuario().length() < 10) throw new CadastroException("A senha deve ter no mínimo 10 caracteres!");
            if (!usuarioCadastro.getSenhaUsuario().matches(".*[a-z]{1,}.*")) throw new CadastroException("A senha deve conter ao menos 1 letra minúscula");
            if (!usuarioCadastro.getSenhaUsuario().matches(".*[A-Z]{1,}.*")) throw new CadastroException("A senha deve conter ao menos 1 letra maiúscula");
            if (!usuarioCadastro.getSenhaUsuario().matches(".*[0-9]{1,}.*")) throw new CadastroException("A senha deve conter ao menos 1 número");
            if (!usuarioCadastro.getSenhaUsuario().matches(".*[^0-9A-Za-z]{1,}.*")) throw new CadastroException("A senha deve conter ao menos 1 caractere especial");

            requestData.put("name", usuarioCadastro.getNomeUsuario());
            requestData.put("email", usuarioCadastro.getEmailUsuario());
            requestData.put("password", usuarioCadastro.getSenhaUsuario());
            JSONObject response = this.backendIntegrator.syncRequest(BackendIntegrator.METHOD_POST, "register", requestData, null);

            if (response == null || response.optString("type").equals("error")) throw new CadastroException("Erro ao cadastrar usuário na API. Mensagem: " + response.toString());

            Log.i("cadastroBusiness", "JSON recebido: " + response);
            usuarioCadastro.setTokenUsuario(response.optString("token"));

            long idUsuario = this.usuarioDao.insert(usuarioCadastro);

            cursor = this.usuarioDao.rawQuery(Query.CONFERE_EXISTENCIA_ID, new String[]{String.valueOf(idUsuario)});

            /**GERAR CHAVE*/

            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex("verificacaoId")) <= 0) throw new CadastroException("Usuário não pôde ser cadastrado");

            Log.i("cadastroBusiness", "Número de usuários cadastrados no banco: " + cursor.getCount());

            retornoCadastro.withResult(usuarioCadastro);

            this.bancoDeDados.setTransactionSuccessful();

        } catch (Throwable error){
            error.printStackTrace();
            Log.i("cadastroBusiness", "Erro ao cadastrar Usuário. Mensagem: " + error.getMessage());
            retornoCadastro.withError(error);
        } finally {
            if (cursor != null) cursor.close();
            this.bancoDeDados.endTransaction();
        }
        return retornoCadastro;
    }

    public interface Query {
        String CONFERE_EXISTENCIA_ID = "SELECT COUNT(*) AS verificacaoId FROM " + Usuario.Metadata.TABLE_NAME
                + " WHERE " + Usuario.Metadata.TABLE_NAME + "." + Usuario.Metadata.FIELD_ID + " = ?";

        String CONFERE_EXISTENCIA_EMAIL = "SELECT COUNT(*) AS verificacaoEmail FROM " + Usuario.Metadata.TABLE_NAME
                + " WHERE " + Usuario.Metadata.TABLE_NAME + "." + Usuario.Metadata.FIELD_EMAIL + " = ?";

        String INSERCAO_USUARIO = "INSERT INTO " + Usuario.Metadata.TABLE_NAME
                + " (" + Usuario.Metadata.FIELD_NOME
                + ", " + Usuario.Metadata.FIELD_EMAIL
                + ", " + Usuario.Metadata.FIELD_SENHA
                + ", " + Usuario.Metadata.FIELD_TOKEN + ") "
                + " VALUES (?,?,?,?)";
    }
}
