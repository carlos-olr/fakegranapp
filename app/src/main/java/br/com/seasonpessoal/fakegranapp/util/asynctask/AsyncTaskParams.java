package br.com.seasonpessoal.fakegranapp.util.asynctask;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by carlos on 09/02/19.
 */
public class AsyncTaskParams implements Serializable {

    private Map<String, Object> parametros;

    public AsyncTaskParams() {
        this.parametros = new HashMap<>();
    }

    public AsyncTaskParams putParam(String chave, Object valor) {
        this.parametros.put(chave, valor);
        return this;
    }

    public <T> T getParam(String chave) {
        return (T) this.parametros.get(chave);
    }

}
