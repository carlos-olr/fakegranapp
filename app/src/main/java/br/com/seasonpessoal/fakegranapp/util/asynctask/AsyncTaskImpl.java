package br.com.seasonpessoal.fakegranapp.util.asynctask;


import android.os.AsyncTask;


/**
 * Created by carlos on 24/02/19.
 */
public abstract class AsyncTaskImpl extends AsyncTask<AsyncTaskParams, AsyncTaskParams, AsyncTaskParams> {

    protected AsyncTaskListener asyncTaskListener;

    public AsyncTaskImpl(AsyncTaskListener asyncTaskListener) {
        this.asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected void onPostExecute(AsyncTaskParams asyncTaskParams) {
        if (asyncTaskListener != null) {
            asyncTaskListener.onFinish(asyncTaskParams);
        }
    }
}
