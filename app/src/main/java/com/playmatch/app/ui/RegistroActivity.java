package com.playmatch.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.playmatch.app.R;


public class RegistroActivity extends AppCompatActivity {

    private ImageView imgFondoLogin;
    private ImageView logoLogin;
    private EditText txtUsuario;
    private EditText txtEmail;
    private EditText txtContraseña;
    private Button btnCrearCuenta;
    private EditText txtPosicionFavorita;
    private EditText txtEdadRegistro;
    private ImageButton btnMostrarContraseña;
    private boolean contraseñaVisible=false;


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
        btnCrearCuenta=findViewById(R.id.btnCrearCuenta);
        txtPosicionFavorita=findViewById(R.id.txtPosicionFavorita);
        txtEdadRegistro=findViewById(R.id.txtEdadRegistro);
        btnMostrarContraseña=findViewById(R.id.btnMostrarContraseña);


        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegistroActivity.this , HomeActivity.class);
                String crearNombreUsuario=txtUsuario.getText().toString();
                intent.putExtra("nombre_usuario",crearNombreUsuario);
                startActivity(intent);
                finish();
            }
        });

        //Iicono mostrar/ocultar contraseña
        btnMostrarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contraseñaVisible){
                    txtContraseña.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnMostrarContraseña.setImageResource(R.drawable.mostrar_contrasena);
                    contraseñaVisible=false;
                }else {
                    txtContraseña.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnMostrarContraseña.setImageResource(R.drawable.ocultar_contrasena);
                    contraseñaVisible=true;
                }
                txtContraseña.setSelection(txtContraseña.getText().length());
            }
        });




    }
}
