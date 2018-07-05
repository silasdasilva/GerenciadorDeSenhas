package com.project.silas.gerenciadordesenhas.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.silas.gerenciadordesenhas.core.annotations.IgnorePersistence;

import java.util.ArrayList;
import java.util.List;

public class Usuario implements Parcelable {

    private long id;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String tokenUsuario;

    private List<String> listaSites;

    private Usuario(Parcel in){
        this.id = in.readLong();
        this.nomeUsuario = in.readString();
        this.emailUsuario = in.readString();
        this.senhaUsuario = in.readString();
        this.tokenUsuario = in.readString();
        in.readList(getListaSites(), ClassLoader.getSystemClassLoader());
    }

    public Usuario() {}

    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public Usuario (Cursor cursor){
        try {
            this.id = cursor.getLong(cursor.getColumnIndex(Metadata.FIELD_ID));
        } catch (Throwable error){
            error.printStackTrace();
            this.id = cursor.getLong(cursor.getColumnIndex(Metadata.PK_ALIAS));
        }
        this.nomeUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_NOME));
        this.emailUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_EMAIL));
        this.senhaUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_SENHA));
        this.tokenUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_TOKEN));

        this.listaSites = new ArrayList<>();
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
    public List<String> getListaSites() {
        return listaSites;
    }

    public Usuario setListaSites(List<String> listaSites) {
        this.listaSites = listaSites;
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
        parcel.writeList(this.listaSites);
    }

    public interface Metadata {
        String TABLE_NAME = "Usuarios";
        String TABLE_ALIAS = "u";
        String PK = "id";
        String PK_ALIAS = "idUsuario";
        String FIELD_ID = "id";
        String FIELD_NOME = "nomeUsuario";
        String FIELD_EMAIL = "emailUsuario";
        String FIELD_SENHA = "senhaUsuario";
        String FIELD_TOKEN = "tokenUsuario";
        String ORDER_BY_ASC = "ASC";
        String ORDER_BY_DESC = "DESC";
    }
}
