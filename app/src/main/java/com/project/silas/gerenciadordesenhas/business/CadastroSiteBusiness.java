package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.BuildConfig;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;
import com.project.silas.gerenciadordesenhas.exceptions.CadastroException;
import com.project.silas.gerenciadordesenhas.exceptions.LoginException;
import com.project.silas.gerenciadordesenhas.repository.database.dao.SiteDao;
import com.project.silas.gerenciadordesenhas.repository.network.BackendIntegrator;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CadastroSiteBusiness {

    private Context contexto;
    private SQLiteDatabase bancoDeDados;
    private Usuario usuarioLogado;

    protected BackendIntegrator backendIntegrator;
    private FileBusiness fileBusiness;

    private SiteDao siteDao;

    public CadastroSiteBusiness (Context context){
        this.contexto = context;
        this.bancoDeDados = InicializacaoBusiness.getDatabase();
        this.siteDao = new SiteDao(this.bancoDeDados);
        this.usuarioLogado = SessionSingletonBusiness.getUsuario();
        this.backendIntegrator = new BackendIntegrator(this.contexto);
        this.fileBusiness = new FileBusiness(this.contexto);
    }

    public OperationResult<Site> insereSite(Site siteInsercao) {

        OperationResult<Site> retornoInsercao = new OperationResult<>();
        Cursor cursor = null;

        try {
            this.bancoDeDados.beginTransaction();

            Log.i("siteBusiness", "Confere dados:\n"
                    + "\nId Usuário: " + this.usuarioLogado.getId()
                    + "\nNome: " + siteInsercao.getNomeSite()
                    + "\nUrl: " + siteInsercao.getUrlSite()
                    + "\nLogin: " + siteInsercao.getLoginSite());

            cursor = this.siteDao.rawQuery(Query.CONFERE_EXISTENCIA_URL_E_LOGIN
                    , new String[]{String.valueOf(this.usuarioLogado.getId()) // WHERE
                            , siteInsercao.getUrlSite() // AND
                            , siteInsercao.getLoginSite()}); // AND

            cursor.moveToFirst();
            if (cursor != null && cursor.getCount() > 0) throw new CadastroException("Já existe esse login para este usuário!");

            long idSite = this.siteDao.insert(siteInsercao.setIdUsuario(String.valueOf(this.usuarioLogado.getId())));

            /*this.bancoDeDados.execSQL(Query.INSERIR_SITE,
                    new String[]{String.valueOf(this.usuarioLogado.getId())
                            , siteInsercao.getUrlSite()
                            , siteInsercao.getLoginSite()
                            , siteInsercao.getSenhaSite()});*/

            cursor = this.siteDao.rawQuery(Query.CONFERE_INSERCAO, new String[]{String.valueOf(idSite)}); // WHERE

            cursor.moveToFirst();
            if (cursor.getCount() <= 0) throw new CadastroException("Site não pôde ser cadastrado");

            Log.i("siteBusiness", "Número de sites cadastrados: " + cursor.getCount());

            retornoInsercao.withResult(siteInsercao);
            this.bancoDeDados.setTransactionSuccessful();
        } catch (Throwable error){
            error.printStackTrace();
            Log.i("siteBusiness", "Erro ao inserir site. Mensagem: " + error.getMessage());
            retornoInsercao.withError(error);
        } finally {
            this.bancoDeDados.endTransaction();
            if (cursor != null) cursor.close();
        }
        return retornoInsercao;
    }

    public OperationResult<Site> atualizaSite(Site siteAtualizacao) {

        OperationResult<Site> retornoAtualizacao = new OperationResult<>();
        Cursor cursor = null;

        try {
            this.bancoDeDados.beginTransaction();

            this.siteDao.update(siteAtualizacao);

            /*this.bancoDeDados.execSQL(Query.ATUALIZAR_SITE,
                    new String[]{siteAtualizacao.getUrlSite()
                            , siteAtualizacao.getLoginSite()
                            , siteAtualizacao.getSenhaSite()
                            , siteAtualizacao.getIdUsuario() // WHERE
                            , String.valueOf(siteAtualizacao.getId())}); // AND*/

            //                                                                              WHERE                                       AND
            cursor = this.siteDao.rawQuery(Query.CONFERE_ATUALIZACAO, new String[]{String.valueOf(this.usuarioLogado.getId()), String.valueOf(siteAtualizacao.getId())});

            cursor.moveToFirst();
            if (cursor.getCount() <= 0) throw new CadastroException("Erro ao atualizar site");

            Site siteAtualizado = new Site(cursor);
            if (!siteAtualizacao.getNomeSite().equals(siteAtualizado.getNomeSite())
                    || !siteAtualizacao.getUrlSite().equals(siteAtualizado.getUrlSite())
                    || !siteAtualizacao.getLoginSite().equals(siteAtualizado.getLoginSite())
                    || !siteAtualizacao.getSenhaSite().equals(siteAtualizado.getSenhaSite())){
                throw new CadastroException("Atualização deu errado");
            }

            Log.i("siteBusiness", "Número de sites atualizados: " + cursor.getCount());

            retornoAtualizacao.withResult(siteAtualizado);
            this.bancoDeDados.setTransactionSuccessful();
        } catch (Throwable error){
            error.printStackTrace();
            Log.i("siteBusiness", "Erro ao inserir site. Mensagem: " + error.getMessage());
            retornoAtualizacao.withError(error);
        } finally {
            this.bancoDeDados.endTransaction();
            if (cursor != null) cursor.close();
        }
        return retornoAtualizacao;
    }

    public OperationResult<Site> excluiSite(Site siteExclusao) {

        OperationResult<Site> retornoExclusao = new OperationResult<>();
        Cursor cursor = null;

        try {
            this.bancoDeDados.beginTransaction();

            //                                                          WHERE                                       AND
            //this.bancoDeDados.execSQL(Query.EXCLUIR_SITE, new String[]{siteExclusao.getIdUsuario(), String.valueOf(siteExclusao.getId())});

            this.siteDao.delete(siteExclusao);

            //                                                                              WHERE                           AND
            //cursor = this.siteDao.rawQuery(Query.CONFERE_EXCLUSAO, new String[]{siteExclusao.getIdUsuario(), String.valueOf(siteExclusao.getId())});

            //cursor.moveToFirst();
            //if (cursor.getCount() > 0) throw new CadastroException("Site não foi excluído");

            retornoExclusao.withResult(null);
            this.bancoDeDados.setTransactionSuccessful();
        } catch (Throwable error){
            error.printStackTrace();
            Log.i("siteBusiness", "Erro ao inserir site. Mensagem: " + error.getMessage());
            retornoExclusao.withError(error);
        } finally {
            this.bancoDeDados.endTransaction();
            if (cursor != null) cursor.close();
        }
        return retornoExclusao;
    }

    public OperationResult<Bitmap> buscaLogoDisco(Site site) {
        OperationResult<Bitmap> retornoLogo = new OperationResult<>();

        try {
            retornoLogo.withResult(this.fileBusiness.buscaLogoDisco(site));
        } catch (Throwable error){
            error.printStackTrace();
            retornoLogo.withError(error);
            Log.i("siteBusiness", "Erro ao buscar foto. Mensagem: " + error.getMessage());
        }
        return retornoLogo;
    }

    public interface Query {


        String INSERIR_SITE = "INSERT INTO " + Site.Metadata.TABLE_NAME
                + " (" + Site.Metadata.FIELD_ID_USUARIO
                + ", " + Site.Metadata.FIELD_URL
                + ", " + Site.Metadata.FIELD_LOGIN
                + ", " + Site.Metadata.FIELD_SENHA + ") "
                + " VALUES (?,?,?,?)";

        String ATUALIZAR_SITE = "UPDATE " + Site.Metadata.TABLE_NAME
                + " SET " + Site.Metadata.FIELD_URL + " = ?"
                + ", " + Site.Metadata.FIELD_LOGIN + " = ?"
                + ", " + Site.Metadata.FIELD_SENHA  + " = ?"
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID_USUARIO + " = ?"
                + " AND " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID + " = ?";

        String EXCLUIR_SITE = "DELETE FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID_USUARIO + " = ?"
                + " AND " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID + " = ?";

        String CONFERE_EXISTENCIA_URL_E_LOGIN = "SELECT * FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID_USUARIO + " = ?"
                + " AND " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_URL + " = ?"
                + " AND " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_LOGIN + " = ?";

        String CONFERE_INSERCAO = "SELECT * FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID + " = ?";

        String CONFERE_ATUALIZACAO = "SELECT * FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID_USUARIO + " = ?"
                + " AND " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID + " = ?";

        String CONFERE_EXCLUSAO = "SELECT COUNT(*) AS verificacaoId FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID_USUARIO + " = ?"
                + " AND " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID + " = ?";
    }
}
