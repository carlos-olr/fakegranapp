package br.com.seasonpessoal.fakegranapp.util.request;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import br.com.seasonpessoal.fakegranapp.util.JSONConverter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by carlos on 26/02/19.
 */
public class RequestHelper {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static class BodyHelper {

        private String json;
        private File arquivo;

        public boolean somenteJSON() {
            return this.json != null && this.arquivo == null;
        }

        public String json() {
            return json;
        }

        public BodyHelper json(String jsonBody) {
            this.json = jsonBody;
            return this;
        }

        public File arquivo() {
            return arquivo;
        }

        public BodyHelper arquivo(File arquivo) {
            this.arquivo = arquivo;
            return this;
        }
    }

    public static Bitmap getImagem(String url) {
        InputStream in = null;
        try {
            in = new java.net.URL(url).openStream();
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("RequestHelper", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T doRequest(String url, String method, BodyHelper body, Map<String, String> headers, Class<T> retorno)
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
            if (body.somenteJSON()) {
                builder.method(method, RequestBody.create(JSON, body.json()));
            } else {
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                multipartBuilder.setType(MultipartBody.FORM)
                    .addFormDataPart("arquivo", body.arquivo.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), body.arquivo))
                    .addFormDataPart("post", body.json);
                builder.method(method, multipartBuilder.build());
            }
        } else {
            builder.method(method, null);
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
