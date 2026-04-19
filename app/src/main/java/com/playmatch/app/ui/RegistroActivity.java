package com.playmatch.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.playmatch.app.R;


public class RegistroActivity extends AppCompatActivity {

    private ImageView imgFondoLogin;
    private ImageView logoLogin;
    private TextView txtUsuario;
    private TextView txtEmail;
    private TextView txtContraseña;
    private Button btnLogin;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Enlazar variables del xml con Java
        imgFondoLogin=findViewById(R.id.imgFondoLogin);
        logoLogin=findViewById(R.id.logoLogin);
        txtUsuario=findViewById(R.id.txtUsuario);
        txtEmail=findViewById(R.id.txtEmail);
        txtContraseña=findViewById(R.id.txtContraseña);
        btnLogin=findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegistroActivity.this , HomeActivity.class);
                String crearNombreUsuario=txtUsuario.getText().toString();
                intent.putExtra("nombre_usuario",crearNombreUsuario);
                startActivity(intent);
                finish();
            }
        });


    }
}
