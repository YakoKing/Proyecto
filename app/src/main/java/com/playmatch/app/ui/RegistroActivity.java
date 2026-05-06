package com.playmatch.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.playmatch.app.ApiServicio.ApiServicio;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.entity.Usuario;
import com.playmatch.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

        //boton crear cuenta
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //guardar lo que el usuario escribe en los campos
                String nombre=txtUsuario.getText().toString().trim();
                String email=txtEmail.getText().toString().trim();
                String contraseña=txtContraseña.getText().toString().trim();
                String posicion=txtPosicionFavorita.getText().toString().trim();
                String edadStr=txtEdadRegistro.getText().toString().trim();

                if (nombre.isEmpty() || email.isEmpty() || contraseña.isEmpty()){

                    Toast.makeText(RegistroActivity.this, "Nombre , email y contraseñas son obligatorios" , Toast.LENGTH_SHORT).show();
                    return;
                }
                Usuario usuario= new Usuario();
                usuario.setNombre(nombre);
                usuario.setEmail(email);
                usuario.setPassword(contraseña);
                if (!posicion.isEmpty()) {
                    usuario.setPosicion(posicion);
                }
                //comprobar que el campo edad no esta vacio porque peta por ser int
                if (!edadStr.isEmpty()) {
                    usuario.setEdad(Integer.parseInt(edadStr));
                };

                btnCrearCuenta.setEnabled(false);
                ApiServicio api= RetrofitCliente.getApiServicio();
                api.registrar(usuario).enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        btnCrearCuenta.setEnabled(true);

                        if (response.isSuccessful() && response.body() !=null){
                            Usuario creado= response.body();
                            SessionManager.getInstance(RegistroActivity.this).guardarSesion(creado.getId()
                            , creado.getNombre() , creado.getEmail() , creado.getEdad(), creado.getPosicion() , creado.getAvatarUrl());
                            Intent intent = new Intent(RegistroActivity.this , HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(RegistroActivity.this, "Error al crear la cuenta: " + response.code(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {

                        btnCrearCuenta.setEnabled(true);
                        Toast.makeText(RegistroActivity.this, "Sin conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
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
