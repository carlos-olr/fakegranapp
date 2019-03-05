package br.com.seasonpessoal.fakegranapp.activity.main;


import java.util.List;

import br.com.seasonpessoal.fakegranapp.bean.PostBean;
import br.com.seasonpessoal.fakegranapp.util.action.DownloadImagemAction;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskListener;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * Created by carlos on 05/03/19.
 */
public class GridPostsAdapter extends BaseAdapter {

    private Context contexto;
    private List<PostBean> posts;

    public GridPostsAdapter(Context context, List<PostBean> posts) {
        this.contexto = context;
        this.posts = posts;
    }

    @Override
    public int getCount() {
        return this.posts.size();
    }

    @Override
    public Object getItem(int position) {
        return this.posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.posts.get(position).hashCode();
    }

    public void atualizarLista(List<PostBean> novosPosts) {
        this.posts.clear();
        this.posts.addAll(novosPosts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        PostBean postBean = (PostBean) this.getItem(position);

        if (convertView == null) {
            int tamanho = ((GridView) parent).getColumnWidth();

            imageView = new ImageView(this.contexto);
            imageView.setLayoutParams(new GridView.LayoutParams(tamanho, tamanho));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }

        AsyncTaskParams params = new AsyncTaskParams();
        params.putParam("url", postBean.getUrlArquivo());
        params.putParam("imageView", imageView);

        new DownloadImagemAction(new AsyncTaskListener() {

            @Override
            public void onFinish(AsyncTaskParams params) {
                ImageView imageView = params.getParam("imageView");
                Bitmap imagem = params.getParam("imagem");
                imageView.setImageBitmap(imagem);

                imageView.invalidate();
            }
        }).execute(params);

        return imageView;
    }
}
