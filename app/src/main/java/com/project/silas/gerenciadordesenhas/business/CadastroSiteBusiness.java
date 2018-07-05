package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.exceptions.CadastroException;
import com.project.silas.gerenciadordesenhas.repository.SiteDao;

public class CadastroSiteBusiness {

    private Context contexto;
    private SQLiteDatabase bancoDeDados;

    private SiteDao siteDao;

    public CadastroSiteBusiness (Context context){
        this.contexto = context;
        this.bancoDeDados = InicializacaoBusiness.getDatabase();
        this.siteDao = new SiteDao(this.bancoDeDados);
    }

    public OperationResult<Site> insereSite(Site siteInsercao) {

        OperationResult<Site> retornoInsercao = new OperationResult<>();
        Cursor cursor = null;

        try {
            this.bancoDeDados.beginTransaction();

            cursor = this.siteDao.rawQuery(Query.CONFERE_EXISTENCIA_URL_E_LOGIN, new String[]{siteInsercao.getUrlSite(), siteInsercao.getLoginSite()});

            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex("verificaExistencia")) > 0) throw new CadastroException("Já existe esse login para este usuário!");

            long idSite = this.siteDao.insert(siteInsercao);

            cursor = this.siteDao.rawQuery(Query.CONFERE_INSERCAO, new String[]{String.valueOf(idSite)});

            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex("verificacaoId")) <= 0) throw new CadastroException("Site não pôde ser cadastrado");

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

            cursor = this.siteDao.rawQuery(Query.BUSCA_SITE_ATUALIZADO, new String[]{String.valueOf(siteAtualizacao.getId())});

            cursor.moveToFirst();
            if (cursor.getCount() <= 0) throw new CadastroException("Erro ao atualizar site");

            Site siteAtualizado = new Site(cursor);
            if (siteAtualizacao.getUrlSite().equals(siteAtualizado.getUrlSite())
                    && siteAtualizacao.getLoginSite().equals(siteAtualizado.getLoginSite())
                    && siteAtualizacao.getSenhaSite().equals(siteAtualizado.getSenhaSite())){
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

            this.siteDao.delete(siteExclusao);

            cursor = this.siteDao.rawQuery(Query.BUSCA_SITE_EXCLUIDO, new String[]{String.valueOf(siteExclusao.getId())});

            cursor.moveToFirst();
            if (cursor.getCount() > 0) throw new CadastroException("Site não foi excluído");

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

    public interface Query {
        String CONFERE_EXISTENCIA_URL_E_LOGIN = "SELECT COUNT(*) AS verificaExistencia FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_URL + " = ?"
                + " AND " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_LOGIN + " = ?";

        String CONFERE_INSERCAO = "SELECT COUNT(*) AS verificacaoId FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID + " = ?";

        String BUSCA_SITE_ATUALIZADO = "SELECT * FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID + " = ?";

        String BUSCA_SITE_EXCLUIDO = "SELECT * FROM " + Site.Metadata.TABLE_NAME
                + " WHERE " + Site.Metadata.TABLE_NAME + "." + Site.Metadata.FIELD_ID + " = ?";
    }
}
