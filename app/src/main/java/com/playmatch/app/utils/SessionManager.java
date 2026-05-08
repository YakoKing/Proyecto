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
    private static final String KEY_USER_EDAD ="edad";
    private static final String KEY_USER_POSICION ="posicion";
    private static final String KEY_USER_AVATAR="avatarUrl";

    private static final String KEY_USER_TELEFONO="telefono";

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;

    /*Sesion manager Api para guardar datos del usuario logueado para tenerlos instantaneos y no
    tener que esperar al servidor */
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

    public void guardarSesion(int id, String nombre, String email, int edad, String posicion , String avatarUrl ,String telefono) {
        sharedPreferences.edit()
                .putInt(KEY_USER_ID, id)
                .putString(KEY_USER_NAME, nombre)
                .putString(KEY_USER_EMAIL, email)
                .putInt(KEY_USER_EDAD, edad)
                .putString(KEY_USER_POSICION, posicion)
                .putString(KEY_USER_AVATAR, avatarUrl)
                .putString(KEY_USER_TELEFONO, telefono)
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
    public int getEdad(){return sharedPreferences.getInt(KEY_USER_EDAD, 0);}

    public String getPosicion(){return sharedPreferences.getString(KEY_USER_POSICION, "");}

    public String getAvatar(){return sharedPreferences.getString(KEY_USER_AVATAR , "");}

    public String getTelefono(){return  sharedPreferences.getString(KEY_USER_TELEFONO , "");}

    public boolean estaLogueado() {
        return getUsuarioId() != -1;
    }

    public void cerrarSesion() {
        sharedPreferences.edit().clear().apply();
    }
}
