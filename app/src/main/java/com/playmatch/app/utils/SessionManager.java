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
    private static final String KEY_USER_REPUTACION = "reputacion";
    private static final String KEY_USER_TELEFONO = "telefono";
    private static final String KEY_USER_AVATAR = "avatar_url";

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

    public void guardarSesion(com.playmatch.app.entity.Usuario usuario) {
        sharedPreferences.edit()
                .putInt(KEY_USER_ID, usuario.getId())
                .putString(KEY_USER_NAME, usuario.getNombre())
                .putString(KEY_USER_EMAIL, usuario.getEmail())
                .putInt(KEY_USER_EDAD, usuario.getEdad())
                .putString(KEY_USER_POSICION, usuario.getPosicion())
                .putFloat(KEY_USER_REPUTACION, (float) usuario.getReputacion())
                .putString(KEY_USER_TELEFONO, usuario.getTelefono())
                .putString(KEY_USER_AVATAR, usuario.getAvatarUrl())
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

    public float getReputacion() {
        return sharedPreferences.getFloat(KEY_USER_REPUTACION, 0.0f);
    }

    public String getTelefono() {
        return sharedPreferences.getString(KEY_USER_TELEFONO, "");
    }

    public String getAvatarUrl() {
        return sharedPreferences.getString(KEY_USER_AVATAR, "");
    }

    public com.playmatch.app.entity.Usuario getUsuario() {
        if (!estaLogueado()) return null;
        com.playmatch.app.entity.Usuario usuario = new com.playmatch.app.entity.Usuario();
        usuario.setId(getUsuarioId());
        usuario.setNombre(getNombre());
        usuario.setEmail(getEmail());
        usuario.setEdad(getEdad());
        usuario.setPosicion(getPosicion());
        usuario.setReputacion(getReputacion());
        usuario.setTelefono(getTelefono());
        usuario.setAvatarUrl(getAvatarUrl());
        return usuario;
    }

    public boolean estaLogueado() {
        return getUsuarioId() != -1;
    }

    public void cerrarSesion() {
        sharedPreferences.edit().clear().apply();
    }
}
