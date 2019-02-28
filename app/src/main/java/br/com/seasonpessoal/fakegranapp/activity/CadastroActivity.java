package br.com.seasonpessoal.fakegranapp.activity;


import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.bean.UsuarioBean;
import br.com.seasonpessoal.fakegranapp.util.SharedPrefsImpl;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskImpl;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskListener;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskParams;
import br.com.seasonpessoal.fakegranapp.util.request.FakegranException;
import br.com.seasonpessoal.fakegranapp.util.request.RequestHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;


public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        this.configurarDefault();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.configurarDefault();
    }

    public void login(View view) {
        if (this.validarLogin()) {
            EditText email = findViewById(R.id.cadastro_login_email_edittext);
            EditText senha = findViewById(R.id.cadastro_login_senha_edittext);

            UsuarioBean usuarioBean = new UsuarioBean();
            usuarioBean.setEmail(email.getText().toString());
            usuarioBean.setSenha(senha.getText().toString());

            String url = getString(R.string.host) + getString(R.string.url_usuario_login);

            AsyncTaskParams params = new AsyncTaskParams().putParam("usuarioBean", usuarioBean).putParam("url", url);

            LoginAction cadastrarAction = new LoginAction(new AsyncTaskListener() {

                @Override
                public void onFinish(AsyncTaskParams result) {
                    String erro = result.getParam("erro");
                    if (erro == null) {
                        SharedPrefsImpl.getInstance(getApplicationContext()).getShardPreferences().edit()
                            .putString("token", (String) result.getParam("token")).apply();
                        startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(CadastroActivity.this, erro, Toast.LENGTH_LONG).show();
                    }
                }
            });
            cadastrarAction.execute(params);
        }
    }

    private boolean validarLogin() {
        String email = ((EditText) findViewById(R.id.cadastro_login_email_edittext)).getText().toString();
        String senha = ((EditText) findViewById(R.id.cadastro_login_senha_edittext)).getText().toString();

        if (Strings.isNullOrEmpty(email) || Strings.isNullOrEmpty(senha)) {
            Toast.makeText(this, "Necessário preencher email e senha", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void cadastrar(View view) {
        if (this.validarCadastro()) {
            EditText nome = findViewById(R.id.cadastro_form_nome_edittext);
            EditText email = findViewById(R.id.cadastro_form_email_edittext);
            EditText senha = findViewById(R.id.cadastro_form_senha_edittext);

            UsuarioBean usuarioBean = new UsuarioBean();
            usuarioBean.setNome(nome.getText().toString());
            usuarioBean.setEmail(email.getText().toString());
            usuarioBean.setSenha(senha.getText().toString());

            String url = getString(R.string.host) + getString(R.string.url_usuario_salvar);

            AsyncTaskParams params = new AsyncTaskParams().putParam("usuarioBean", usuarioBean).putParam("url", url);

            CadastrarAction cadastrarAction = new CadastrarAction(new AsyncTaskListener() {

                @Override
                public void onFinish(AsyncTaskParams asyncTaskParams) {
                    String erro = asyncTaskParams.getParam("erro");
                    if (erro == null) {
                        mostrarLogin(null);
                        Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG)
                            .show();
                    } else {
                        Toast.makeText(CadastroActivity.this, erro, Toast.LENGTH_LONG).show();
                    }
                }
            });
            cadastrarAction.execute(params);
        }
    }

    private static class LoginAction extends AsyncTaskImpl {

        public LoginAction(AsyncTaskListener asyncTaskListener) {
            super(asyncTaskListener);
        }

        @Override
        protected AsyncTaskParams doInBackground(AsyncTaskParams... asyncTaskParams) {
            AsyncTaskParams param = asyncTaskParams[0];
            String url = param.getParam("url");

            try {
                //String json = "{\"email\": " + JSONConverter.toJSON(param.getParam("usuarioBean")) + "}";
                UsuarioBean usuarioBean = param.getParam("usuarioBean");
                JSONObject json = new JSONObject();
                json.put("email", usuarioBean.getEmail());
                json.put("senha", usuarioBean.getSenha());

                Map<String, String> headers = Maps.newHashMap();
                headers.put("Content-Type", "application/json");

                String token = RequestHelper.doRequest(url, "POST", json.toString(), headers, String.class);
                param.putParam("token", token);
            } catch (FakegranException e) {
                param.putParam("erro", e.getMessage());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return param;
        }
    }

    private static class CadastrarAction extends AsyncTaskImpl {

        CadastrarAction(AsyncTaskListener asyncTaskListener) {
            super(asyncTaskListener);
        }

        @Override
        protected AsyncTaskParams doInBackground(AsyncTaskParams... asyncTaskParams) {
            AsyncTaskParams param = asyncTaskParams[0];
            String url = param.getParam("url");

            try {
                String json = param.getParam("usuarioBean").toString();
                Map<String, String> headers = Maps.newHashMap();
                headers.put("Content-Type", "application/json");

                RequestHelper.doRequest(url, "POST", json, headers, String.class);
            } catch (FakegranException e) {
                param.putParam("erro", e.getMessage());
            }
            return param;
        }
    }

    private boolean validarCadastro() {
        String senha = ((EditText) findViewById(R.id.cadastro_form_senha_edittext)).getText().toString();
        String confirmarSenha =
            ((EditText) findViewById(R.id.cadastro_form_confirmarsenha_edittext)).getText().toString();

        if (Strings.isNullOrEmpty(senha) || Strings.isNullOrEmpty(confirmarSenha)) {
            Toast.makeText(this, "Necessário preencher senha", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (senha.length() < 6 || confirmarSenha.length() < 6) {
            Toast.makeText(this, "Senha precisa ter 6 digítos", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Objects.equal(senha, confirmarSenha)) {
            Toast.makeText(this, "Senha e Confirmar Senha estão diferentes", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void mostrarCadastro(View botao) {
        View botoesIniciaisLayout = findViewById(R.id.cadastro_botoes_iniciais_layout);
        View cadastroLayout = findViewById(R.id.cadastro_cadastro_layout);

        cadastroLayout.setVisibility(View.VISIBLE);
        botoesIniciaisLayout.setVisibility(View.GONE);
    }

    public void mostrarLogin(View botao) {
        View botoesIniciaisLayout = findViewById(R.id.cadastro_botoes_iniciais_layout);
        View loginLayout = findViewById(R.id.cadastro_login_layout);

        loginLayout.setVisibility(View.VISIBLE);
        botoesIniciaisLayout.setVisibility(View.GONE);
    }

    private void configurarDefault() {
        View loginLayout = findViewById(R.id.cadastro_login_layout);
        View cadastroLayout = findViewById(R.id.cadastro_cadastro_layout);
        View botoesIniciaisLayout = findViewById(R.id.cadastro_botoes_iniciais_layout);

        loginLayout.setVisibility(View.GONE);
        cadastroLayout.setVisibility(View.GONE);
        botoesIniciaisLayout.setVisibility(View.VISIBLE);
    }
}
