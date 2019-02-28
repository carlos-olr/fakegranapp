package br.com.seasonpessoal.fakegranapp.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.common.base.Strings;


/**
 * Created by carlos on 24/02/19.
 */
public class SharedPrefsImpl {

    private static SharedPrefsImpl instance;
    private SharedPreferences sharedPreferences;

    private SharedPrefsImpl(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPrefsImpl getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsImpl(context);
        }
        return instance;
    }

    public SharedPreferences getShardPreferences() {
        return this.sharedPreferences;
    }

    public void putToJson(String chave, Object object) {
        this.sharedPreferences.edit().putString(chave, JSONConverter.toJSON(object)).apply();
    }

    public <T> T getFromJson(String chave, Class<T> klass) {
        String json = this.sharedPreferences.getString(chave, null);
        if (!Strings.isNullOrEmpty(json)) {
            return JSONConverter.fromJSON(json, klass);
        }
        return null;
    }

}
