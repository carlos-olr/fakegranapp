package br.com.seasonpessoal.fakegranapp.util.request;


import java.io.IOException;
import java.util.Map;

import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.util.JSONConverter;

import android.util.Log;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by carlos on 26/02/19.
 */
public class RequestHelper {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static <T> T doRequest(String url, String method, String body, Map<String, String> headers, Class<T> retorno)
        throws FakegranException {
        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (headers != null) {
            for (Map.Entry<String, String> chaveValor : headers.entrySet()) {
                builder.addHeader(chaveValor.getKey(), chaveValor.getValue());
            }
        }
        if (body != null) {
            builder.method(method, RequestBody.create(JSON, body));
        }

        try {
            Response response = client.newCall(builder.build()).execute();
            if (response.body() == null) {
                Log.e("RequestHelper", "resposta null");
                throw new RuntimeException();
            }
            if (response.code() == 200) {
                return JSONConverter.fromJSON(response.body().string(), retorno);
            }
            String erro = response.body().string();
            Log.e("RequestHelper", erro);
            throw new FakegranException(erro);
        } catch (IOException e) {
            Log.e("RequestHelper", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

}
