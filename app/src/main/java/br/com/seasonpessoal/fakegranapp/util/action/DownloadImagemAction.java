package br.com.seasonpessoal.fakegranapp.util.action;


import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskImpl;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskListener;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskParams;
import br.com.seasonpessoal.fakegranapp.util.request.RequestHelper;

import android.graphics.Bitmap;


/**
 * Created by carlos on 05/03/19.
 */
public class DownloadImagemAction extends AsyncTaskImpl {

    public DownloadImagemAction(AsyncTaskListener asyncTaskListener) {
        super(asyncTaskListener);
    }

    @Override
    protected AsyncTaskParams doInBackground(AsyncTaskParams... params) {
        AsyncTaskParams param = params[0];
        String url = param.getParam("url");

        Bitmap imagem = RequestHelper.getImagem(url);
        param.putParam("imagem", imagem);

        return param;
    }
}
