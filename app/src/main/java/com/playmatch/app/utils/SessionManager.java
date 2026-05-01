package com.playmatch.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {
    private static final String PREF_NAME = "sesion_segura";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "nombre";
    private static final String KEY_USER_EMAIL = "email";

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;

    private SessionManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e("SessionManager", "Error al inicializar EncryptedSharedPreferences", e);
            // Fallback a SharedPreferences normales si falla el cifrado*
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void guardarSesion(int id, String nombre, String email) {
        sharedPreferences.edit()
                .putInt(KEY_USER_ID, id)
                .putString(KEY_USER_NAME, nombre)
                .putString(KEY_USER_EMAIL, email)
                .apply();
    }

    public int getUsuarioId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getNombre() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public boolean estaLogueado() {
        return getUsuarioId() != -1;
    }

    public void cerrarSesion() {
        sharedPreferences.edit().clear().apply();
    }
}
