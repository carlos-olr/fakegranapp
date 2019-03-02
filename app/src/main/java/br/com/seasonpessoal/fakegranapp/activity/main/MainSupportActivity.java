package br.com.seasonpessoal.fakegranapp.activity.main;


import java.util.List;

import br.com.seasonpessoal.fakegranapp.R;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.google.common.collect.Lists;


/**
 * Created by carlos on 02/03/19.
 */
public abstract class MainSupportActivity extends AppCompatActivity {

    static final int REQUEST_PERMISSOES_FOTO = 151;
    static final int REQUEST_TIRAR_FOTO = 152;

    static final String OPERACAO_SELECIONAR_TELA_PRINCIPAL = "selecionarTelaPrincipal";

    abstract <T> T executarOperacao(String operacao);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().post(new Runnable() {

            @Override
            public void run() {
                for (Integer botaoId : BOTOES) {
                    View v = findViewById(botaoId);
                    if (v != null) {
                        v.setOnTouchListener(new AdicionarEfeitoClickOnTouhListener());
                    }
                }
            }

        });
    }

    private List<Integer> BOTOES = Lists.newArrayList(
        R.id.main_home_btn, R.id.main_grid_btn, R.id.main_camera_btn, R.id.main_conta_btn
    );

    private class AdicionarEfeitoClickOnTouhListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    ImageView view = (ImageView) v;
                    view.setBackgroundResource(R.color.efeitoClick);
                    view.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    ImageView view = (ImageView) v;
                    view.setBackgroundResource(R.color.branco);
                    view.invalidate();
                    break;
                }
            }

            return v.performClick();
        }
    }

    protected class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int tela) {
            switch (tela) {
                case 0:
                    break;
                case 1:
                    executarOperacao(OPERACAO_SELECIONAR_TELA_PRINCIPAL);
                    break;
                case 2:
                    break;
                default:
                    throw new RuntimeException("Tela não conhecida:" + tela);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String LAYOUT_SELECIONADO = "LAYOUT_SELECIONADO";

        public static PlaceholderFragment newInstance(int tela) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            int layoutSelecionado;
            switch (tela) {
                case 0:
                    layoutSelecionado = R.layout.fragment_main_camerastories;
                    break;
                case 1:
                    layoutSelecionado = R.layout.fragment_main_principal;
                    break;
                case 2:
                    layoutSelecionado = R.layout.fragment_main_conversas;
                    break;
                default:
                    throw new RuntimeException("Tela não conhecida:" + tela);
            }
            args.putInt(LAYOUT_SELECIONADO, layoutSelecionado);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int layoutSelecionado = getArguments().getInt(LAYOUT_SELECIONADO);
            return inflater.inflate(layoutSelecionado, container, false);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    protected void alterarEstiloBotaoSelecionado(View view) {
        ImageView botao = (ImageView) view;
        switch (botao.getId()) {
            case R.id.main_home_btn
        }
        botao.setImageDrawable(getDrawable(R.drawable.icon_account_selecionado));

    }

    protected void mostrarConteudo(View conteudo) {
        RelativeLayout layoutConteudo = findViewById(R.id.main_principal_conteudo_layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layoutConteudo.getWidth(), layoutConteudo.getHeight());
        layoutConteudo.removeAllViewsInLayout();
        layoutConteudo.addView(conteudo, params);
    }
}
