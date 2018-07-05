package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.CadastroException;
import com.project.silas.gerenciadordesenhas.repository.UsuarioDao;

public class CadastroUsuariosBusiness {

    private Context contexto;
    private SQLiteDatabase bancoDeDados;
    private UsuarioDao usuarioDao;

    public CadastroUsuariosBusiness (Context context){
        this.contexto = context;
        this.bancoDeDados = InicializacaoBusiness.getDatabase();
        this.usuarioDao = new UsuarioDao(this.bancoDeDados);
    }

    public OperationResult<Usuario> cadastrarUsuario(Usuario usuarioCadastro) {

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
            if (cursor.getCount() > 0) throw new CadastroException("Já existe um Usuário com esse email cadastrado");

            /**
             * Verificação da senha
             *
             * Mínimo de 8 caracteres
             * Mínimo de 1 letra
             * Mínimo de 1 número
             * Mínimo de 1 caractere especial
             *
             * */

            if (usuarioCadastro.getSenhaUsuario().length() < 8) throw new CadastroException("A senha deve ter no mínimo 8 caracteres!");
            if (!usuarioCadastro.getSenhaUsuario().matches(".*[A-Za-z]{1,}[0-9]*[^0-9A-Za-z]*")) throw new CadastroException("A senha deve conter ao menos 1 letra");
            if (!usuarioCadastro.getSenhaUsuario().matches(".*[0-9]{1,}[A-Za-z]*[^0-9A-Za-z]*")) throw new CadastroException("A senha deve conter ao menos 1 número");
            if (!usuarioCadastro.getSenhaUsuario().matches(".*[^0-9A-Za-z]{1,}[A-Za-z]*[0-9]*")) throw new CadastroException("A senha deve conter ao menos 1 caractere especial");

            // Se tudo certo insere usuario
            long idUsuario = this.usuarioDao.insert(usuarioCadastro);

            cursor = this.usuarioDao.rawQuery(Query.CONFERE_INSERCAO_USUARIO, new String[]{String.valueOf(idUsuario)});

            if (cursor.getCount() <= 0) throw new CadastroException("Nenhum usuário cadastrado");

            cursor.moveToFirst();
            Log.i("cadastroBusiness", "Número de usuários cadastrados: " + cursor.getCount());
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
        String CONFERE_EXISTENCIA_EMAIL = "SELECT COUNT(*) FROM " + Usuario.Metadata.TABLE_NAME
                + " WHERE " + Usuario.Metadata.TABLE_NAME + "." + Usuario.Metadata.FIELD_EMAIL + " = ?";

        String CONFERE_INSERCAO_USUARIO = "SELECT COUNT(*) FROM " + Usuario.Metadata.TABLE_NAME
                + " WHERE " + Usuario.Metadata.TABLE_NAME + "." + Usuario.Metadata.FIELD_ID + " = ?";
    }
}
