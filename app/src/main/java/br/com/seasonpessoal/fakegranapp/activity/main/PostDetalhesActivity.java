package br.com.seasonpessoal.fakegranapp.activity.main;


import java.util.Map;

import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.bean.PostBean;
import br.com.seasonpessoal.fakegranapp.bean.UsuarioBean;
import br.com.seasonpessoal.fakegranapp.database.UsuarioEntity;
import br.com.seasonpessoal.fakegranapp.util.action.DownloadImagemAction;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskImpl;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskListener;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskParams;
import br.com.seasonpessoal.fakegranapp.util.request.FakegranException;
import br.com.seasonpessoal.fakegranapp.util.request.RequestHelper;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.collect.Maps;
import com.orm.SugarRecord;


public class PostDetalhesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detalhes);

        findViewById(R.id.loading_post_detalhes).setVisibility(View.VISIBLE);
        findViewById(R.id.post_detalhes_descricao).setVisibility(View.GONE);
        findViewById(R.id.post_detalhes_imagem).setVisibility(View.GONE);
        findViewById(R.id.post_detalhes_criador).setVisibility(View.GONE);

        PostBean postBean = (PostBean) getIntent().getSerializableExtra("post");

        TextView descricaoTextView = findViewById(R.id.post_detalhes_descricao);
        descricaoTextView.setText(postBean.getDescricao());

        String urldetalhesusuario = getString(R.string.host) + getString(R.string.url_usuario_get);
        urldetalhesusuario = urldetalhesusuario.replace("idusuario", postBean.getCriador());
        urldetalhesusuario += "?campo=login";

        UsuarioEntity usuarioLogado = SugarRecord.findAll(UsuarioEntity.class).next();

        AsyncTaskParams paramsDetalhes = new AsyncTaskParams();
        paramsDetalhes.putParam("urlimagem", postBean.getUrlArquivo());
        paramsDetalhes.putParam("urldetalhesusuario", urldetalhesusuario);
        paramsDetalhes.putParam("token", usuarioLogado.getToken());

        new CarregarDetalhesPost(new AsyncTaskListener() {

            @Override
            public void onFinish(AsyncTaskParams resultado) {
                UsuarioBean usuario = resultado.getParam("usuario");

                TextView criadorTextView = findViewById(R.id.post_detalhes_criador);
                criadorTextView.setText(usuario.getLogin());
                criadorTextView.invalidate();

                ImageView imagemImageView = findViewById(R.id.post_detalhes_imagem);

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int tamanhoPadrao = size.x;

                Bitmap imagem = resultado.getParam("imagem");
                imagemImageView.setImageBitmap(imagem);
                imagemImageView.getLayoutParams().width = tamanhoPadrao;
                imagemImageView.getLayoutParams().height = tamanhoPadrao;
                imagemImageView.requestLayout();
                imagemImageView.invalidate();

                findViewById(R.id.loading_post_detalhes).setVisibility(View.GONE);
                findViewById(R.id.post_detalhes_descricao).setVisibility(View.VISIBLE);
                findViewById(R.id.post_detalhes_imagem).setVisibility(View.VISIBLE);
                findViewById(R.id.post_detalhes_criador).setVisibility(View.VISIBLE);
            }
        }).execute(paramsDetalhes);

    }

    private static class CarregarDetalhesPost extends AsyncTaskImpl {

        public CarregarDetalhesPost(AsyncTaskListener asyncTaskListener) {
            super(asyncTaskListener);
        }

        @Override
        protected AsyncTaskParams doInBackground(AsyncTaskParams... params) {
            AsyncTaskParams param = params[0];
            String urlimagem = param.getParam("urlimagem");
            String urlDetalhesUsuario = param.getParam("urldetalhesusuario");
            String token = param.getParam("token");

            Map<String, String> headers = Maps.newHashMap();
            headers.put("Authorization", "Bearer " + token);

            UsuarioBean usuarioBean = null;
            Bitmap imagem = null;
            try {
                usuarioBean = RequestHelper.doRequest(urlDetalhesUsuario, "GET", null, headers, UsuarioBean.class);
                imagem = RequestHelper.getImagem(urlimagem);
            } catch (FakegranException e) {
                throw new RuntimeException(e);
            }
            param.putParam("usuario", usuarioBean);
            param.putParam("imagem", imagem);

            return param;
        }
    }

}
