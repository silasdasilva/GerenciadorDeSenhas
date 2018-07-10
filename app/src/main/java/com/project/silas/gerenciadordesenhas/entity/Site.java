package com.project.silas.gerenciadordesenhas.entity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.silas.gerenciadordesenhas.core.annotations.IgnorePersistence;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Site implements Parcelable {

    private Long id;
    private Usuario usuario;
    private String idUsuario;

    private Bitmap logoSite;
    private String caminhoFoto;

    private String nomeSite;

    private String urlSite;
    private String loginSite;
    private String senhaSite;

    public Site (){}

    public Site (Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(Metadata.FIELD_ID));
        this.idUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_ID_USUARIO));
        this.caminhoFoto = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_CAMINHO_FOTO_LOGO));
        this.nomeSite = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_NOME));
        this.urlSite = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_URL));
        this.loginSite = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_LOGIN));
        this.senhaSite = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_SENHA));
    }

    public Site (JSONObject jsonObject) {
        this.nomeSite = jsonObject.optString(Metadata.JSONFIELD_NOME);
        this.urlSite = jsonObject.optString(Metadata.JSONFIELD_URL);
        this.loginSite = jsonObject.optString(Metadata.JSONFIELD_LOGIN);
        this.senhaSite = jsonObject.optString(Metadata.JSONFIELD_SENHA);
    }

    protected Site(Parcel in) {
        if (in.readByte() == 0) {
            this.id = null;
        } else {
            this.id = in.readLong();
        }
        this.idUsuario = in.readString();
        this.logoSite = in.readParcelable(ClassLoader.getSystemClassLoader());
        this.caminhoFoto = in.readString();
        this.nomeSite = in.readString();
        this.urlSite = in.readString();
        this.loginSite = in.readString();
        this.senhaSite = in.readString();
    }

    public static final Creator<Site> CREATOR = new Creator<Site>() {
        @Override
        public Site createFromParcel(Parcel in) {
            return new Site(in);
        }

        @Override
        public Site[] newArray(int size) {
            return new Site[size];
        }
    };

    public Long getId() {
        return id;
    }

    public Site setId(Long id) {
        this.id = id;
        return this;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public Site setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }

    @IgnorePersistence
    public Bitmap getLogoSite() {
        return logoSite;
    }

    public Site setLogoSite(Bitmap logoSite) {
        this.logoSite = logoSite;
        return this;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public Site setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
        return this;
    }

    public String getNomeSite() {
        return nomeSite;
    }

    public Site setNomeSite(String nomeSite) {
        this.nomeSite = nomeSite;
        return this;
    }

    public String getUrlSite() {
        return urlSite;
    }

    public Site setUrlSite(String urlSite) {
        this.urlSite = urlSite;
        return this;
    }

    public String getLoginSite() {
        return loginSite;
    }

    public Site setLoginSite(String loginSite) {
        this.loginSite = loginSite;
        return this;
    }

    public String getSenhaSite() {
        return senhaSite;
    }

    public Site setSenhaSite(String senhaSite) {
        this.senhaSite = senhaSite;
        return this;
    }

    @IgnorePersistence
    public Usuario getUsuario() {
        return usuario;
    }

    public Site setUsuario(Usuario usuario) {
        this.usuario = usuario;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeParcelable(this.usuario, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable((Parcelable) this.logoSite, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeString(this.idUsuario);
        dest.writeString(this.caminhoFoto);
        dest.writeString(this.nomeSite);
        dest.writeString(this.urlSite);
        dest.writeString(this.loginSite);
        dest.writeString(this.senhaSite);
    }

    public interface Metadata {
        String TABLE_NAME = "Site";
        String TABLE_ALIAS = "s";
        String PK = "id";
        String PK_ALIAS = "idSite";
        String FIELD_ID = "id";
        String FIELD_ID_USUARIO = "idUsuario";
        String FIELD_CAMINHO_FOTO_LOGO = "caminhoFoto";
        String FIELD_NOME = "nomeSite";
        String FIELD_URL = "urlSite";
        String FIELD_LOGIN = "loginSite";
        String FIELD_SENHA = "senhaSite";
        String ORDER_BY_ASC = "ASC";
        String ORDER_BY_DESC = "DESC";

        String JSONFIELD_NOME = "name";
        String JSONFIELD_URL = "url";
        String JSONFIELD_LOGIN = "email";
        String JSONFIELD_SENHA = "password";
    }
}
