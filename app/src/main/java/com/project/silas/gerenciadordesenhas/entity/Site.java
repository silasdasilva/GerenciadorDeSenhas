package com.project.silas.gerenciadordesenhas.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.silas.gerenciadordesenhas.core.annotations.IgnorePersistence;

import java.util.ArrayList;
import java.util.List;

public class Site implements Parcelable {

    private long id;
    private Usuario usuario;
    private String idUsuario;
    private String urlSite;
    private String loginSite;
    private String senhaSite;

    private Site(Parcel in){
        this.id = in.readLong();
        this.usuario = in.readParcelable(ClassLoader.getSystemClassLoader());
        this.idUsuario = in.readString();
        this.urlSite = in.readString();
        this.loginSite = in.readString();
        this.senhaSite = in.readString();
    }

    public static final Parcelable.Creator<Site> CREATOR = new Parcelable.Creator<Site>() {
        public Site createFromParcel(Parcel in) {
            return new Site(in);
        }

        public Site[] newArray(int size) {
            return new Site[size];
        }
    };

    public Site (){}

    public Site (Cursor cursor){
        try {
            this.id = cursor.getLong(cursor.getColumnIndex(Metadata.FIELD_ID));
        } catch (Throwable error){
            error.printStackTrace();
            this.id = cursor.getLong(cursor.getColumnIndex(Metadata.PK_ALIAS));
        }
        this.idUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_ID_USUARIO));
        this.urlSite = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_URL));
        this.loginSite = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_LOGIN));
        this.senhaSite = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_SENHA));

    }

    public long getId() {
        return id;
    }

    public Site setId(long id) {
        this.id = id;
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

    public String getIdUsuario() {
        return idUsuario;
    }

    public Site setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeParcelable(this.usuario, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeString(this.idUsuario);
        parcel.writeString(this.urlSite);
        parcel.writeString(this.loginSite);
        parcel.writeString(this.senhaSite);
    }

    public interface Metadata {
        String TABLE_NAME = "Sites";
        String TABLE_ALIAS = "s";
        String PK = "id";
        String PK_ALIAS = "idSite";
        String FIELD_ID = "id";
        String FIELD_ID_USUARIO = "idUsuario";
        String FIELD_URL = "urlSalva";
        String FIELD_LOGIN = "emailSalvo";
        String FIELD_SENHA = "senhaSalva";
        String ORDER_BY_ASC = "ASC";
        String ORDER_BY_DESC = "DESC";
    }
}
