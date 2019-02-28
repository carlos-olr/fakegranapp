package br.com.seasonpessoal.fakegranapp.activity;


import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.bean.UsuarioBean;
import br.com.seasonpessoal.fakegranapp.util.SharedPrefsImpl;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskImpl;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskParams;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.google.common.collect.Lists;
import com.orm.SugarRecord;


public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ImageView logo = findViewById(R.id.loading_logo_imgview);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        animation.setDuration(500);
        logo.startAnimation(animation);

        new Handler().postDelayed(new TrocarActivityRunner(), 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new TrocarActivityRunner(), 500);
    }

    private class TrocarActivityRunner implements Runnable {

        @Override
        public void run() {
            //if (Lists.newArrayList(SugarRecord.findAll(UsuarioBean.class)).size() == 1) {
            if (false) {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(LoadingActivity.this, CadastroActivity.class));
            }
        }
    }
}
