package br.com.seasonpessoal.fakegranapp;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.common.collect.Lists;
import com.orm.SugarRecord;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.List;

import br.com.seasonpessoal.fakegranapp.bean.UsuarioBean;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("br.com.seasonpessoal.fakegranapp", appContext.getPackageName());

        UsuarioBean usuarioBean = new UsuarioBean();
        usuarioBean.save();

        List<UsuarioBean> usuarios = Lists.newArrayList(SugarRecord.findAll(UsuarioBean.class));
        assertEquals(1, usuarios.size());
    }
}
