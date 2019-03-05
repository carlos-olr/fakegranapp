package br.com.seasonpessoal.fakegranapp.database;


import java.io.Serializable;

import br.com.seasonpessoal.fakegranapp.bean.UsuarioBean;
import br.com.seasonpessoal.fakegranapp.util.JSONConverter;

import android.util.Base64;
import com.orm.SugarRecord;


/**
 * Created by carlos on 28/02/19.
 */
public class UsuarioEntity extends SugarRecord<UsuarioEntity> implements Serializable {

    private String token;

    public UsuarioEntity() {
    }

    public UsuarioEntity(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public UsuarioBean getUsuarioBean() {
        String json = new String(Base64.decode(this.token.split("\\.")[1], Base64.DEFAULT));
        return JSONConverter.fromJSON(json, UsuarioBean.class);
    }

}
