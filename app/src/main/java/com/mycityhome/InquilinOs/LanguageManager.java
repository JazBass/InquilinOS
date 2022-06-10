package com.mycityhome.InquilinOs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import java.util.Locale;

public class LanguageManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    public LanguageManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LANG", Context.MODE_PRIVATE);
    }
    
    public void updateLanguge(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        context.getApplicationContext().createConfigurationContext(config);
        setLang(language);
        Log.i("lang", language);
    }

    private void setLang(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LANG", language);
        editor.apply();
    }
}
