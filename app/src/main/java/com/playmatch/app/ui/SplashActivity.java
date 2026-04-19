package com.playmatch.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.playmatch.app.R;

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

                if (progreso<100){
                    handler.postDelayed(this,100);
                }else{
                    Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
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
    }



}
