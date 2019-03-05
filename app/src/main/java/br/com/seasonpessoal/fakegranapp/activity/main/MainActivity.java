package br.com.seasonpessoal.fakegranapp.activity.main;


import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.bean.PostBean;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orm.SugarRecord;


public class MainActivity extends MainSupportActivity {

    private UsuarioEntity usuario;
    private ViewPager paginador;
    private File arquivoFoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usuario = SugarRecord.findAll(UsuarioEntity.class).next();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        paginador = findViewById(R.id.main_paginador);
        paginador.setAdapter(mSectionsPagerAdapter);
        paginador.addOnPageChangeListener(new PageListener());

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                paginador.setCurrentItem(1);
            }
        }, 100);
    }

    @Override
    <T> T executarOperacao(String operacao) {
        switch (operacao) {
            case OPERACAO_SELECIONAR_TELA_PRINCIPAL:
                selecionarTelaPrincipal();
                return null;
            default:
                throw new RuntimeException("operacao não conhecida");
        }
    }

    private void selecionarTelaPrincipal() {
        this.selecionarFeedTelaPrincipal(null);
    }

    public void selecionarFeedTelaPrincipal(View view) {
        mostrarConteudo(R.layout.fragment_main_principal_pagina_feed, BotaoMenu.FEED);
    }

    public void selecionarCameraTelaPrincipal(View view) {
        verificarPermissoesETirarFoto();
    }

    public void selecionarGridTelaPrincipal(View view) {
        mostrarConteudo(R.layout.fragment_main_principal_pagina_grid, BotaoMenu.GRID);
    }

    public void selecionarContaTelaPrincipal(View view) {
        mostrarConteudo(R.layout.fragment_main_principal_pagina_conta, BotaoMenu.CONTA);

        UsuarioBean usuarioBean = this.usuario.getUsuarioBean();
        TextView nomeText = findViewById(R.id.conta_nome_text);
        TextView emailText = findViewById(R.id.conta_email_text);

        nomeText.setText(usuarioBean.getNome());
        emailText.setText(usuarioBean.getEmail());

        GridView grid = findViewById(R.id.conta_posts_grid);
        String token = this.usuario.getToken();
        String id = this.usuario.getUsuarioBean().getIdHash();
        final GridPostsAdapter adapter = new GridPostsAdapter(this, Lists.<PostBean>newArrayList());
        grid.setAdapter(adapter);

        String url = getString(R.string.host) + getString(R.string.url_posts_usuario).replace("idusuario", id);

        AsyncTaskParams params = new AsyncTaskParams().putParam("url", url).putParam("token", token);

        new CarregarPostsUsuarioAction(new AsyncTaskListener() {

            @Override
            public void onFinish(AsyncTaskParams params) {
                PostBean[] posts = params.getParam("posts");

                adapter.atualizarLista(Lists.newArrayList(posts));
                adapter.notifyDataSetChanged();
            }
        }).execute(params);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostBean post = (PostBean) adapter.getItem(position);

                Intent intent = new Intent(MainActivity.this, PostDetalhesActivity.class);
                intent.putExtra("post", post);
                startActivity(intent);
            }
        });

    }

    private static class CarregarPostsUsuarioAction extends AsyncTaskImpl {

        CarregarPostsUsuarioAction(AsyncTaskListener asyncTaskListener) {
            super(asyncTaskListener);
        }

        @Override
        protected AsyncTaskParams doInBackground(AsyncTaskParams... params) {
            AsyncTaskParams param = params[0];
            String url = param.getParam("url");
            String token = param.getParam("token");

            Map<String, String> headers = Maps.newHashMap();
            headers.put("Authorization", "Bearer " + token);

            PostBean[] posts = null;
            try {
                posts = RequestHelper.doRequest(url, "GET", null, headers, PostBean[].class);
            } catch (FakegranException e) {
                throw new RuntimeException(e);
            }
            param.putParam("posts", posts);
            return param;
        }
    }

    private void verificarPermissoesETirarFoto() {
        boolean camera =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;

        if (camera) {
            ActivityCompat
                .requestPermissions(this, new String[] { Manifest.permission.CAMERA }, REQUEST_PERMISSOES_FOTO);
        } else {
            executarIntentParaTirarFotos();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
        @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSOES_FOTO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    executarIntentParaTirarFotos();
                } else {
                    Toast.makeText(this, "Não vai funcionar!!!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void enviarPost(View view) {
        EditText editTextDescricao = findViewById(R.id.main_principal_pagina_foto_descricao);
        String descricao = editTextDescricao.getText().toString();
        if (Strings.isNullOrEmpty(descricao)) {
            Toast.makeText(this, "A descrição está vazia", Toast.LENGTH_LONG).show();
            return;
        }

        PostBean post = new PostBean();
        post.setCriador(usuario.getUsuarioBean().getIdHash());
        post.setDescricao(descricao);

        String url = getString(R.string.host) + getString(R.string.url_post_criar);

        AsyncTaskParams params = new AsyncTaskParams();
        params.putParam("arquivo", arquivoFoto);
        params.putParam("post", post);
        params.putParam("token", usuario.getToken());
        params.putParam("url", url);

        new EnviarPostAction(new AsyncTaskListener() {

            @Override
            public void onFinish(AsyncTaskParams retorno) {
                if (retorno.getParam("erro") != null) {
                    throw new RuntimeException((String) retorno.getParam("erro"));
                }
                Toast.makeText(MainActivity.this, "Salvou!", Toast.LENGTH_LONG).show();
            }
        }).execute(params);
    }

    private static class EnviarPostAction extends AsyncTaskImpl {

        EnviarPostAction(AsyncTaskListener asyncTaskListener) {
            super(asyncTaskListener);
        }

        @Override
        protected AsyncTaskParams doInBackground(AsyncTaskParams... asyncTaskParams) {
            AsyncTaskParams param = asyncTaskParams[0];
            File arquivo = param.getParam("arquivo");
            PostBean post = param.getParam("post");
            String token = param.getParam("token");
            String url = param.getParam("url");

            try {
                RequestHelper.BodyHelper body = new RequestHelper.BodyHelper();
                body.json(post.toString()).arquivo(arquivo);

                Map<String, String> headers = Maps.newHashMap();
                headers.put("Authorization", "Bearer " + token);

                PostBean postSalvo = RequestHelper.doRequest(url, "POST", body, headers, PostBean.class);
                param.putParam("post", postSalvo);
            } catch (FakegranException e) {
                param.putParam("erro", e.getMessage());
            }
            return param;
        }
    }

    private String uriArquivo = null;

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
                    mostrarConteudo(R.layout.fragment_main_principal_pagina_camera, BotaoMenu.CAMERA);

                    try {
                        final InputStream inStreamFoto = getContentResolver().openInputStream(Uri.parse(uriArquivo));
                        getWindow().getDecorView().post(new Runnable() {

                            @Override
                            public void run() {
                                View v = findViewById(R.id.main_principal_pagina_foto_botao);
                                if (v != null) {
                                    v.setOnTouchListener(
                                        new AdicionarEfeitoClickOnTouhListener(R.drawable.rounded_shape_branco,
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

    public void abrirChat(View view) {
        paginador.setCurrentItem(2);
    }
}
