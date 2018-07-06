package com.project.silas.gerenciadordesenhas.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.silas.gerenciadordesenhas.core.annotations.IgnorePersistence;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private Long id;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String tokenUsuario;

    public Usuario() {}

    public Usuario (Cursor cursor){
        this.id = cursor.getLong(cursor.getColumnIndex(Metadata.FIELD_ID));
        this.nomeUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_NOME));
        this.emailUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_EMAIL));
        this.senhaUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_SENHA));
        this.tokenUsuario = cursor.getString(cursor.getColumnIndex(Metadata.FIELD_TOKEN));
    }

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
    }
}
