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

import com.project.silas.gerenciadordesenhas.R;
import com.project.silas.gerenciadordesenhas.core.OperationResult;
import com.project.silas.gerenciadordesenhas.core.abstracts.BusinessAbstract;
import com.project.silas.gerenciadordesenhas.entity.Site;
import com.project.silas.gerenciadordesenhas.entity.Usuario;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ansilva on 01/12/2016.
 */
public class FileBusiness extends BusinessAbstract {

    public static final String ROOT = Environment.getExternalStorageDirectory() + "/Gerenciador de Senhas";

    private Context contexto;
    private Usuario usuarioLogado;

    private SQLiteDatabase banco;

    public FileBusiness(Context context) {
        super(context);
        this.contexto = context;
        this.usuarioLogado = SessionSingletonBusiness.getUsuario();
        this.banco = InicializacaoBusiness.getDatabase();
    }

    public Bitmap salvarLogoSite(Bitmap logoSite, Site site) {

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

            FileOutputStream out = new FileOutputStream(file);
            logoSite.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Throwable e) {
            e.printStackTrace();
            Log.i("fotoBusiness", "Erro ao salvar foto. Mensagem: " + e.getMessage());
        }
        return logoSite;
    }

    public Bitmap buscaLogoDisco(Site siteFotoBuscar){
        Bitmap fotoLogo = null;

        try {
            File caminho = new File(siteFotoBuscar.getCaminhoFoto());

            if (caminho.exists()){
                fotoLogo = BitmapFactory.decodeFile(caminho.getAbsolutePath());
            }

        } catch (Throwable error){
            error.printStackTrace();
            Log.i("fotoBusiness", "Nenhuma foto encontrada no banco. Mensagem: " + error.getMessage());
        }
        return fotoLogo;
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

    /**
     * Tenta descobrir e retorna o mimetype do arquivo recebido baseado na sua extensão.
     * @param arquivo
     * @return
     */
    public String getMimeType(File arquivo) {
        Map<String, String> lista = new HashMap<>();
        lista.put("jpg", "image/jpeg");
        lista.put("jpeg", "image/jpeg");
        lista.put("gif", "image/gif");
        lista.put("png", "image/png");
        lista.put("zip", "application/zip");
        lista.put("pdf", "application/pdf");
        String nome = arquivo.getName();
        String[] parts = arquivo.getName().split("\\.");
        if(!lista.containsKey(parts[parts.length-1])) {
            throw new InvalidParameterException("Parametro invalido");
        }
        return lista.get(parts[parts.length - 1]);
    }

    /**
     * Retorna o MD5 do File recebido via parâmetro
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public String getMd5Arquivo(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        int byteArraySize = 2048;
        InputStream is = new FileInputStream(file);
        md.reset();
        byte[] bytes = new byte[byteArraySize];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            md.update(bytes, 0, numBytes);
        }
        byte[] digest = md.digest();
        String result = new String(Hex.encodeHex(digest));
        return result;
    }
}
