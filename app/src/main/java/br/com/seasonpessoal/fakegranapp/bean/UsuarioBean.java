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
    private String login;
    private String senha;
    @JsonProperty("exp")
    private Long dataExpiracao;
    @JsonProperty("iat")
    private Long dataLogin;

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

    public Long getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Long dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public Long getDataLogin() {
        return dataLogin;
    }

    public void setDataLogin(Long dataLogin) {
        this.dataLogin = dataLogin;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "{\"usuario\": " + JSONConverter.toJSON(this) + "}";
    }
}
