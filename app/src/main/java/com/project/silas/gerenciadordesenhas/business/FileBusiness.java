package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.BusinessAbstract;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by ansilva on 01/12/2016.
 */
public class FileBusiness extends BusinessAbstract {

    public static final String ROOT = Environment.getExternalStorageDirectory() + "/Gerenciador de Senhas";
    public static String caminhoUltimaFoto;

    private Context contexto;
    private Usuario usuarioLogado;

    private SQLiteDatabase banco;

    public FileBusiness(Context context) {
        super(context);
        this.contexto = context;
        this.usuarioLogado = SessionSingletonBusiness.getUsuario();
        this.banco = InicializacaoBusiness.getDatabase();
    }

    public OperationResult<Bitmap> salvarLogoSite(Drawable logoSite, Site site) {
        OperationResult<Bitmap> retornoFotoInserir = new OperationResult<>();

        Date data = new Date();
        //Criação do diretório com o root, usuario
        File direct = new File(ROOT + "/" + SessionSingletonBusiness.getUsuario().getId());

        //Verificando se ele já existe e criando ele caso não exista
        if (!direct.exists()) direct.mkdirs();

        //Criação do nome da logo do site
        File file = new File(direct + "/", site.getNomeSite() + ".jpg");

        //Caso a foto já exista acrescenta-se underline mais um número em sequência
        if (file.exists()) {
            int numeroSeparador = 1;
            String textoSeparador = "_";
            String textoNumeroSeparador = textoSeparador + numeroSeparador;
            file = new File(direct + "/" + site.getNomeSite() + textoNumeroSeparador + ".jpg");

            if (file.exists()) {
                while (file.exists()) {
                    numeroSeparador++;
                    textoNumeroSeparador = textoSeparador + numeroSeparador;
                    file = new File(direct + "/" + site.getNomeSite() + textoNumeroSeparador + ".jpg");
                }
            }
        }

        Log.i("fotoBusiness", "Caminho da foto completo no Business: " + file.getAbsolutePath());

        try {
            this.banco.beginTransaction();

            FileOutputStream out = new FileOutputStream(file);
            Bitmap fotoLogoSite = ((BitmapDrawable) logoSite).getBitmap();
            fotoLogoSite.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            retornoFotoInserir.withResult(fotoLogoSite);
            this.banco.setTransactionSuccessful();
        } catch (Throwable e) {
            e.printStackTrace();
            Log.i("fotoBusiness", "Erro ao salvar foto. Mensagem: " + e.getMessage());
            retornoFotoInserir.withError(e);
        } finally {
            this.banco.endTransaction();
        }
        return retornoFotoInserir;
    }

    public OperationResult<Bitmap> buscaLogoSiteDisco (Site siteFotoBuscar){
        OperationResult<Bitmap> retornoFotoBuscar = new OperationResult<>();
        File caminho = null;
        Bitmap fotoLogo = null;

        try {
            this.banco.beginTransaction();

            caminho = new File(siteFotoBuscar.getCaminhoFoto());

            if (caminho.exists()){
                fotoLogo = BitmapFactory.decodeFile(caminho.getAbsolutePath());
            }

            retornoFotoBuscar.withResult(fotoLogo);
            this.banco.setTransactionSuccessful();
        } catch (Throwable error){
            error.printStackTrace();
            retornoFotoBuscar.withError(error);
            Log.i("fotoBusiness", "Nenhuma foto encontrada no banco. Mensagem: " + error.getMessage());
        } finally {
            this.banco.endTransaction();
        }
        return retornoFotoBuscar;
    }

    public OperationResult<Site> deletarFotoLogoSite(Site siteFotoDeletar){
        OperationResult<Site> retornoFotoDeletada = new OperationResult<>();

        try {

            File caminhoFotoDeletar = new File(siteFotoDeletar.getCaminhoFoto());
            if (caminhoFotoDeletar.exists()) caminhoFotoDeletar.delete();
            siteFotoDeletar.setCaminhoFoto(null); //Apaga caminho da foto

            retornoFotoDeletada.withResult(null);
        } catch (Throwable error){
            error.printStackTrace();
            retornoFotoDeletada.withError(error);
        }
        return retornoFotoDeletada;
    }
}
