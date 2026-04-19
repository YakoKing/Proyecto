package com.playmatch.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.playmatch.app.R;

public class LoginActivity  extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignUp;
    private ImageButton imgButtonInsta;
    private ImageButton imgButtonX;
    private EditText txtContraseña;
    private EditText txtUsuario;
    private ImageView logoLogin , imgFondoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Enlazar las variables del xml con java
        btnLogin=findViewById(R.id.btnLogin);
        btnSignUp=findViewById(R.id.btnSignUp);
        imgButtonInsta=findViewById(R.id.imgButtonInsta);
        imgButtonX=findViewById(R.id.imgButtonX);
        txtContraseña=findViewById(R.id.txtContraseña);
        txtUsuario=findViewById(R.id.txtUsuario);
        logoLogin=findViewById(R.id.logoLogin);
        imgFondoLogin=findViewById(R.id.imgFondoLogin);

        //Boton de instagram
        imgButtonInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.instagram.com/ivanrdgz99/"));
                startActivity(intent);
            }
        });

        //Boton de twitter
        imgButtonX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://x.com/MrYaKoSmG"));
                startActivity(intent);
            }
        });

        //Boton de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*con el objeto Intent hacemos la accion,primer parametro desde donde se ejecuta
                segundo parametro donde queremos ir*/
                Intent intent= new Intent(LoginActivity.this, HomeActivity.class);

                //pasamos el nombre de usuario a la pantalla del home
                String nombreUsuario=txtUsuario.getText().toString();
                intent.putExtra("nombre_usuario",nombreUsuario);
                startActivity(intent);
                finish();

            }
        });

        //Boton de crear cuenta
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this , RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}
