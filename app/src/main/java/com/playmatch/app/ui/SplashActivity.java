package com.playmatch.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.playmatch.app.ApiServicio.ApiServicio;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.entity.Partido;
import com.playmatch.app.entity.Usuario;
import com.playmatch.app.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity  extends AppCompatActivity {

    //Variables usadas en el xml
    private ImageView imgFondo;
    private ImageView imgLogo;
    private ProgressBar barra;
    private TextView txtTexto;

    private int progreso=0; //variable para controlar el proceso de la barra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Enlazar las variables del xml con java

        imgFondo=findViewById(R.id.imgFondo);
        imgLogo=findViewById(R.id.imgLogo);
        barra=findViewById(R.id.barra);
        txtTexto=findViewById(R.id.txtTexto);

        barra.setMax(100);
        barra.setProgress(0);

        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                progreso=progreso+2;
                barra.setProgress(progreso);

                if (progreso < 100) {
                    handler.postDelayed(this, 100);
                } else {
                    // Comprobar si hay una sesión activa segura
                    SessionManager sessionManager = SessionManager.getInstance(SplashActivity.this);

                    Intent intent;
                    if (sessionManager.estaLogueado()) {
                        // Sesión activa, ir a Home
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("nombre_usuario", sessionManager.getNombre());
                    } else {
                        // Sin sesión, ir a Login
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }

                //texto animado
                int puntos=(progreso/10)%4;
                String texto="Cargando";
                for (int i=0;i<puntos;i++){
                    texto=texto +".";
                }
                txtTexto.setText(texto);
            }
        };

        handler.postDelayed(runnable,100);

        RetrofitCliente.getApiServicio().getUsuarios().enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Usuario u : response.body()) {
                        Log.d("USUARIOS", "ID: " + u.getId() + " | Nombre: " + u.getNombre() + " | Email: " + u.getEmail());
                    }
                } else {
                    Log.e("USUARIOS", "Respuesta vacía o error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("USUARIOS", "Error de conexión: " + t.getMessage());
            }
        });


    }



}
