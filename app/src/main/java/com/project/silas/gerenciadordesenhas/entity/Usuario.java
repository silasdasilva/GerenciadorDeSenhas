package com.project.silas.gerenciadordesenhas.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.silas.gerenciadordesenhas.core.annotations.IgnorePersistence;

import java.util.List;

public class Usuario implements Parcelable {

    private long id;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String tokenUsuario;

    private List<String> listaUrlsUsuario;

    private Usuario(Parcel in){
        this.id = in.readLong();
        this.nomeUsuario = in.readString();
        this.emailUsuario = in.readString();
        this.senhaUsuario = in.readString();
        this.tokenUsuario = in.readString();
        in.readList(getListaUrlsUsuario(), ClassLoader.getSystemClassLoader());
    }

    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public Usuario (Cursor cursor){
        id = cursor.getLong(cursor.getColumnIndex(Metadata.FIELD_ID));
        nomeUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_NOME));
        emailUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_EMAIL));
        senhaUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_SENHA));
        tokenUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_TOKEN));
    }

    public long getId() {
        return id;
    }

    public Usuario setId(long id) {
        this.id = id;
        return this;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public Usuario setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
        return this;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public Usuario setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
        return this;
    }

    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public Usuario setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
        return this;
    }

    public String getTokenUsuario() {
        return tokenUsuario;
    }

    public Usuario setTokenUsuario(String tokenUsuario) {
        this.tokenUsuario = tokenUsuario;
        return this;
    }

    @IgnorePersistence
    public List<String> getListaUrlsUsuario() {
        return listaUrlsUsuario;
    }

    public Usuario setListaUrlsUsuario(List<String> listaUrlsUsuario) {
        this.listaUrlsUsuario = listaUrlsUsuario;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.nomeUsuario);
        parcel.writeString(this.emailUsuario);
        parcel.writeString(this.senhaUsuario);
        parcel.writeString(this.tokenUsuario);
        parcel.writeList(this.listaUrlsUsuario);
    }

    public interface Metadata {
        public static final String TABLE_NAME = "Usuario";
        public static final String TABLE_ALIAS = "u";
        public static final String PK = "id";
        public static final String PK_ALIAS = "idUsuario";
        public static final String FIELD_ID = "id";
        public static final String FIELD_NOME = "nomeUsuario";
        public static final String FIELD_EMAIL = "emailUsuario";
        public static final String FIELD_SENHA = "senhaUsuario";
        public static final String FIELD_TOKEN = "tokenUsuario";
        public static final String ORDER_BY = "nomeUsuario ASC";
    }
}
