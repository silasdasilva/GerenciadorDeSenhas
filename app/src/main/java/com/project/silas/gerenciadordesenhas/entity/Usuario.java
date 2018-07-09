package com.project.silas.gerenciadordesenhas.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.silas.gerenciadordesenhas.core.annotations.IgnorePersistence;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Usuario implements Parcelable{

    private Long id;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String tokenUsuario;

    public Usuario() {}

    public Usuario(JSONObject jsonObject) {
        nomeUsuario = jsonObject.optString(Metadata.JSON_FIELD_NOME);
        emailUsuario = jsonObject.optString(Metadata.JSON_FIELD_EMAIL);
        senhaUsuario = jsonObject.optString(Metadata.JSON_FIELD_SENHA);
        tokenUsuario = jsonObject.optString(Metadata.JSON_FIELD_TOKEN);
    }

    public Usuario (Cursor cursor){
        this.id = cursor.getLong(cursor.getColumnIndex(Metadata.FIELD_ID));
        this.nomeUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_NOME));
        this.emailUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_EMAIL));
        this.senhaUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_SENHA));
        this.tokenUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_TOKEN));
    }

    protected Usuario(Parcel in) {
        if (in.readByte() == 0) {
            this.id = null;
        } else {
            this.id = in.readLong();
        }
        this.nomeUsuario = in.readString();
        this.emailUsuario = in.readString();
        this.senhaUsuario = in.readString();
        this.tokenUsuario = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public Long getId() {
        return id;
    }

    public Usuario setId(Long id) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (this.id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(this.id);
        }
        dest.writeString(this.nomeUsuario);
        dest.writeString(this.emailUsuario);
        dest.writeString(this.senhaUsuario);
        dest.writeString(this.tokenUsuario);
    }

    public interface Metadata {
        String TABLE_NAME = "Usuario";
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

        //Nome dos campos da forma que vêem do servidor.
        String JSON_FIELD_NOME = "name";
        String JSON_FIELD_EMAIL = "email";
        String JSON_FIELD_SENHA = "password";
        String JSON_FIELD_TOKEN = "token";
    }
}
