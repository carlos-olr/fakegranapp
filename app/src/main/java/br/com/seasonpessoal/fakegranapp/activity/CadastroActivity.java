package br.com.seasonpessoal.fakegranapp.activity;


import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.activity.main.MainActivity;
import br.com.seasonpessoal.fakegranapp.activity.main.MainSupportActivity;
import br.com.seasonpessoal.fakegranapp.bean.UsuarioBean;
import br.com.seasonpessoal.fakegranapp.database.UsuarioEntity;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskImpl;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskListener;
import br.com.seasonpessoal.fakegranapp.util.asynctask.AsyncTaskParams;
import br.com.seasonpessoal.fakegranapp.util.request.FakegranException;
import br.com.seasonpessoal.fakegranapp.util.request.RequestHelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
                        UsuarioEntity usuarioEntity = new UsuarioEntity((String) result.getParam("token"));
                        usuarioEntity.save();

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
            EditText login = findViewById(R.id.cadastro_form_login_edittext);
            EditText senha = findViewById(R.id.cadastro_form_senha_edittext);

            UsuarioBean usuarioBean = new UsuarioBean();
            usuarioBean.setNome(nome.getText().toString());
            usuarioBean.setEmail(email.getText().toString());
            usuarioBean.setLogin(login.getText().toString());
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

    static final int REQUEST_PERMISSOES_FOTO = 151;
    static final int REQUEST_TIRAR_FOTO = 152;

    public void tirarFotoPerfil(View view) {
        boolean camera =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;

        if (camera) {
            ActivityCompat
                .requestPermissions(this, new String[] { Manifest.permission.CAMERA }, REQUEST_PERMISSOES_FOTO);
        } else {
            executarIntentParaTirarFotos();
        }
    }

    private String uriArquivo = null;
    private File arquivoFoto = null;

    private void executarIntentParaTirarFotos() {
        Intent tirarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tirarFotoIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File diretorio = new File(getFilesDir(), "/fotostiradas");
                if (!diretorio.exists()) {
                    diretorio.mkdirs();
                }
                long agora = new Date().getTime();
                arquivoFoto = File.createTempFile(String.valueOf(agora), ".jpg", diretorio);
                uriArquivo = "file:" + arquivoFoto.getAbsolutePath();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName(), arquivoFoto);
            tirarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            tirarFotoIntent.putExtra("android.intent.extra.quickCapture", true);

            startActivityForResult(tirarFotoIntent, REQUEST_TIRAR_FOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TIRAR_FOTO) {
            switch (resultCode) {
                case RESULT_OK:
                    mostrarConteudo(R.layout.fragment_main_principal_pagina_camera, MainSupportActivity.BotaoMenu.CAMERA);

                    try {
                        final InputStream inStreamFoto = getContentResolver().openInputStream(Uri.parse(uriArquivo));
                        getWindow().getDecorView().post(new Runnable() {

                            @Override
                            public void run() {
                                View v = findViewById(R.id.main_principal_pagina_foto_botao);
                                if (v != null) {
                                    v.setOnTouchListener(
                                        new MainSupportActivity.AdicionarEfeitoClickOnTouhListener(R.drawable.rounded_shape_branco,
                                            R.drawable.rounded_shape_efeito_click));
                                }

                                ImageView imageView = findViewById(R.id.main_principal_pagina_camera_preview);

                                Bitmap fotoBitmap = BitmapFactory.decodeStream(inStreamFoto);
                                imageView.setImageBitmap(fotoBitmap);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case RESULT_CANCELED:
                    selecionarTelaPrincipal();
                    break;
                default:
                    throw new RuntimeException("ResultCode " + resultCode + " não mapeado");
            }
        }
    }

    private static class LoginAction extends AsyncTaskImpl {

        LoginAction(AsyncTaskListener asyncTaskListener) {
            super(asyncTaskListener);
        }

        @Override
        protected AsyncTaskParams doInBackground(AsyncTaskParams... asyncTaskParams) {
            AsyncTaskParams param = asyncTaskParams[0];
            String url = param.getParam("url");

            try {
                UsuarioBean usuarioBean = param.getParam("usuarioBean");
                JSONObject json = new JSONObject();
                json.put("email", usuarioBean.getEmail());
                json.put("senha", usuarioBean.getSenha());

                Map<String, String> headers = Maps.newHashMap();
                headers.put("Content-Type", "application/json");

                RequestHelper.BodyHelper body = new RequestHelper.BodyHelper().json(json.toString());
                String token = RequestHelper.doRequest(url, "POST", body, headers, String.class);
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

                RequestHelper.BodyHelper body = new RequestHelper.BodyHelper().json(json);
                RequestHelper.doRequest(url, "POST", body, headers, String.class);
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
        this.esconderTudo();
        View cadastroLayout = findViewById(R.id.cadastro_cadastro_layout);

        cadastroLayout.setVisibility(View.VISIBLE);
    }

    public void mostrarLogin(View botao) {
        this.esconderTudo();
        View loginLayout = findViewById(R.id.cadastro_login_layout);

        loginLayout.setVisibility(View.VISIBLE);
    }

    private void configurarDefault() {
        this.esconderTudo();
        View botoesIniciaisLayout = findViewById(R.id.cadastro_botoes_iniciais_layout);

        botoesIniciaisLayout.setVisibility(View.VISIBLE);
    }

    private void esconderTudo() {
        View loginLayout = findViewById(R.id.cadastro_login_layout);
        View cadastroLayout = findViewById(R.id.cadastro_cadastro_layout);
        View botoesIniciaisLayout = findViewById(R.id.cadastro_botoes_iniciais_layout);

        loginLayout.setVisibility(View.GONE);
        cadastroLayout.setVisibility(View.GONE);
        botoesIniciaisLayout.setVisibility(View.GONE);
    }
}
