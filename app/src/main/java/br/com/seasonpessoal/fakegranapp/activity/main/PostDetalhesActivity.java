package br.com.seasonpessoal.fakegranapp.activity.main;


import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.bean.PostBean;
import br.com.seasonpessoal.fakegranapp.util.action.DownloadImagemAction;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskListener;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskParams;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class PostDetalhesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detalhes);

        PostBean postBean = (PostBean) getIntent().getSerializableExtra("post");

        TextView criadorTextView = findViewById(R.id.post_detalhes_criador);
        TextView descricaoTextView = findViewById(R.id.post_detalhes_descricao);

        criadorTextView.setText(postBean.getCriador());
        descricaoTextView.setText(postBean.getDescricao());

        AsyncTaskParams params = new AsyncTaskParams();
        params.putParam("url", postBean.getUrlArquivo());

        new DownloadImagemAction(new AsyncTaskListener() {

            @Override
            public void onFinish(AsyncTaskParams resultado) {
                ImageView imagemImageView = findViewById(R.id.post_detalhes_imagem);

                Bitmap imagem = resultado.getParam("imagem");
                imagemImageView.setImageBitmap(imagem);
                imagemImageView.getLayoutParams().width = imagem.getWidth();
                imagemImageView.getLayoutParams().height = imagem.getWidth();
                imagemImageView.requestLayout();
                imagemImageView.invalidate();
            }
        }).execute(params);
    }
}
