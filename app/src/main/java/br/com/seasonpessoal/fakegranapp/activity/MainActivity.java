package br.com.seasonpessoal.fakegranapp.activity;


import br.com.seasonpessoal.fakegranapp.R;
import br.com.seasonpessoal.fakegranapp.database.UsuarioEntity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.orm.SugarRecord;


public class MainActivity extends AppCompatActivity {

    private ViewPager paginador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UsuarioEntity usuario = SugarRecord.findAll(UsuarioEntity.class).next();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        paginador = findViewById(R.id.container);
        paginador.setAdapter(mSectionsPagerAdapter);
        paginador.addOnPageChangeListener(new PageListener());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                paginador.setCurrentItem(1);
            }
        }, 100);
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int tela) {
            switch (tela) {
                case 0:
                    break;
                case 1:
                    // Tela principal
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
}
