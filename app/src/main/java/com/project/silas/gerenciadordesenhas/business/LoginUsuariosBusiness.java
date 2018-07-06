package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.LoginException;

public class LoginUsuariosBusiness {

    private Context contexto;
    private SQLiteDatabase banco;

    public LoginUsuariosBusiness (Context context){
        this.contexto = context;
        this.banco = InicializacaoBusiness.getDatabase();
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

            cursor = this.banco.rawQuery(Query.BUSCA_USUARIOS_CADASTRADOS, new String[]{usuarioLogar.getEmailUsuario()});

            if (cursor == null || cursor.getCount() <= 0 || cursor instanceof CursorIndexOutOfBoundsException) throw new LoginException("Este e-mail não pertence a nenhum Usuário. Tente novamente!");

            cursor.moveToFirst();
            Usuario usuario = new Usuario(cursor);
            Log.i("loginBusiness", "Confere caracteres banco:\n"
                    + "\nId: " + usuario.getId()
                    + "\nNome: " + usuario.getNomeUsuario()
                    + "\nE-mail: " + usuario.getEmailUsuario()
                    + "\nSenha: " + usuario.getSenhaUsuario()
            );
            if (!usuario.getSenhaUsuario().equals(usuarioLogar.getSenhaUsuario())) throw new LoginException("Senha incorreta!");
            retornoLogin.withResult(usuario);

        } catch (Throwable error){
            error.printStackTrace();
            Log.i("loginBusiness", "Erro ao logar. Mensagem: " + error.getMessage());
            retornoLogin.withError(error);
        } finally {
            if (cursor != null) cursor.close();
        }
        return retornoLogin;
    }

    public interface Query {
        String BUSCA_USUARIOS_CADASTRADOS = "SELECT * FROM " + Usuario.Metadata.TABLE_NAME
                + " WHERE " + Usuario.Metadata.TABLE_NAME + "." + Usuario.Metadata.FIELD_EMAIL + " = ?";
    }
}
