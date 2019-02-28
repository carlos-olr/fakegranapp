package br.com.seasonpessoal.fakegranapp.activity;


import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.util.SharedPrefsImpl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = SharedPrefsImpl.getInstance(getApplicationContext()).getShardPreferences()
            .getString("token", "token vazio");

        TextView textView = findViewById(R.id.main_token_edittext);
        textView.setText(token);

        String json = new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT));

        textView = findViewById(R.id.main_json_edittext);
        textView.setText(json);
    }
}
