package br.com.seasonpessoal.fakegranapp.bean;


import java.io.Serializable;

import br.com.seasonpessoal.fakegranapp.util.JSONConverter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.orm.SugarRecord;


/**
 * Created by carlos on 24/02/19.
 */
public class UsuarioBean implements Serializable {

    @JsonProperty("_id")
    private String idHash;
    private String nome;
    private String email;
    private String senha;
    private String payload;

    public String getIdHash() {
        return idHash;
    }

    public void setIdHash(String idHash) {
        this.idHash = idHash;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "{\"usuario\": " + JSONConverter.toJSON(this) + "}";
    }
}
