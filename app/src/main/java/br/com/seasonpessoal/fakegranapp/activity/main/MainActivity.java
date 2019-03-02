package br.com.seasonpessoal.fakegranapp.activity.main;


import java.io.File;
import java.io.InputStream;
import java.util.Date;

import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.database.UsuarioEntity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.orm.SugarRecord;


public class MainActivity extends MainSupportActivity {

    private UsuarioEntity usuario;
    private ViewPager paginador;

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
        this.selecionarFeedTelaPrincipal(findViewById(R.id.main_home_btn));
    }

    public void selecionarFeedTelaPrincipal(View view) {
        View conteudo = LayoutInflater.from(MainActivity.this).inflate(R.layout.fragment_main_principal_pagina_feed, null);
        ImageView botao = (ImageView) view;
        botao.setImageDrawable(getDrawable(R.drawable.icon_feed_selecionado));

        mostrarConteudo(conteudo);
    }

    public void selecionarCameraTelaPrincipal(View view) {
        View conteudo = LayoutInflater.from(MainActivity.this).inflate(R.layout.fragment_main_principal_pagina_camera, null);
        ImageView botao = (ImageView) view;
        botao.setImageDrawable(getDrawable(R.drawable.icon_camera_selecionado));

        mostrarConteudo(conteudo);
        verificarPermissoesETirarFoto();
    }

    public void selecionarGridTelaPrincipal(View view) {
        View conteudo = LayoutInflater.from(MainActivity.this).inflate(R.layout.fragment_main_principal_pagina_grid, null);
        ImageView botao = (ImageView) view;
        botao.setImageDrawable(getDrawable(R.drawable.icon_grid_selecionado));

        mostrarConteudo(conteudo);
    }

    public void selecionarContaTelaPrincipal(View view) {
        View conteudo = LayoutInflater.from(MainActivity.this).inflate(R.layout.fragment_main_principal_pagina_conta, null);
        ImageView botao = (ImageView) view;
        botao.setImageDrawable(getDrawable(R.drawable.icon_account_selecionado));

        mostrarConteudo(conteudo);
    }

    private void verificarPermissoesETirarFoto() {
        boolean camera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;

        if (camera) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA}, REQUEST_PERMISSOES_FOTO);
        } else {
            executarIntentParaTirarFotos();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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

    }

    private String uriArquivo = null;

    private void executarIntentParaTirarFotos() {
        Intent tirarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tirarFotoIntent.resolveActivity(getPackageManager()) != null) {
            File arquivoFoto = null;
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
            startActivityForResult(tirarFotoIntent, REQUEST_TIRAR_FOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TIRAR_FOTO && resultCode == RESULT_OK) {
            try {
                ImageView imageView = findViewById(R.id.main_principal_pagina_camera_preview);
                InputStream inStreamFoto = getContentResolver().openInputStream(Uri.parse(uriArquivo));
                Bitmap fotoBitmap = BitmapFactory.decodeStream(inStreamFoto);
                imageView.setImageBitmap(fotoBitmap);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
