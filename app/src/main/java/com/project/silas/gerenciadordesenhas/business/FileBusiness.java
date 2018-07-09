package com.project.silas.gerenciadordesenhas.business;

import android.content.Context;
import android.os.Environment;

import com.project.silas.gerenciadordesenhas.core.abstracts.BusinessAbstract;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class FileBusiness extends BusinessAbstract {

    public FileBusiness(Context context) {
        super(context);
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
            throw new InvalidParameterException("exceptions_mimetype_invalido");
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
