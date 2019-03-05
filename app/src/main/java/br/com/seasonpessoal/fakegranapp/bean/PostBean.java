package br.com.seasonpessoal.fakegranapp.bean;


import static com.google.common.base.Objects.*;

import java.io.Serializable;

import br.com.seasonpessoal.fakegranapp.util.JSONConverter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;


/**
 * Created by carlos on 04/03/19.
 */
public class PostBean implements Serializable {

    @JsonProperty("_id")
    private String id;
    private String criador;
    private Long dataCriacao;
    private String urlArquivo;
    private String descricao;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCriador() {
        return criador;
    }

    public void setCriador(String criador) {
        this.criador = criador;
    }

    public Long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getUrlArquivo() {
        return urlArquivo;
    }

    public void setUrlArquivo(String urlArquivo) {
        this.urlArquivo = urlArquivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostBean that = (PostBean) o;
        return equal(this.id, that.id) && equal(this.criador, that.criador) && equal(this.dataCriacao, that.dataCriacao)
            && equal(this.urlArquivo, that.urlArquivo) && equal(this.descricao, that.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.criador, this.dataCriacao, this.urlArquivo, this.descricao);
    }

    @Override
    public String toString() {
        return JSONConverter.toJSON(this);
    }
}
