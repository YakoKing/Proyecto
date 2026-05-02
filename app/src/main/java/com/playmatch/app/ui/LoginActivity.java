package com.playmatch.app.ui;
//dfsdfsdfsdfsdfffsdfsdfsdfsdfsdfsdfdsfdssdffsddfs
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.playmatch.app.ApiServicio.LoginRequest;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.entity.Usuario;
import com.playmatch.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity  extends AppCompatActivity {


    private Button btnLogin;
    private Button btnSignUp;
    private ImageButton imgButtonInsta;
    private ImageButton imgButtonX;
    private EditText txtContraseña;
    private EditText txtUsuario;
    private ImageView logoLogin , imgFondoLogin;
    private TextView txtErrorLogin;
    private ImageView imgErrorUsuario;
    private ImageView imgErrorContraseña;
    private ImageButton btnVerContraseña;
    private boolean contraseñaVisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Enlazar las variables del xml con java
        btnLogin=findViewById(R.id.btnCrearCuenta);
        btnSignUp=findViewById(R.id.btnSignUp);
        imgButtonInsta=findViewById(R.id.imgButtonInsta);
        imgButtonX=findViewById(R.id.imgButtonX);
        txtContraseña=findViewById(R.id.txtContraseña);
        txtUsuario=findViewById(R.id.txtUsuario);
        logoLogin=findViewById(R.id.logoLogin);
        imgFondoLogin=findViewById(R.id.imgFondoLogin);
        txtErrorLogin=findViewById(R.id.txtErrorLogin);
        imgErrorContraseña=findViewById(R.id.imgErrorContraseña);
        imgErrorUsuario=findViewById(R.id.imgErrorUsuario);
        btnVerContraseña=findViewById(R.id.btnVerContraseña);

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

        //Boton ver/ocultar contraseña
        btnVerContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contraseñaVisible){
                    txtContraseña.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnVerContraseña.setImageResource(R.drawable.mostrar_contrasena);
                    contraseñaVisible=false;
                }else {
                    txtContraseña.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnVerContraseña.setImageResource(R.drawable.ocultar_contrasena);
                    contraseñaVisible=true;
                    }
                    txtContraseña.setSelection(txtContraseña.getText().length());
                }


        });

        //Boton de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*con el objeto Intent hacemos la accion,primer parametro desde donde se ejecuta
                segundo parametro donde queremos ir*/
                /*Intent intent= new Intent(LoginActivity.this, HomeActivity.class);

                //pasamos el nombre de usuario a la pantalla del home
                String nombreUsuario=txtUsuario.getText().toString();
                intent.putExtra("nombre_usuario",nombreUsuario);
                startActivity(intent);
                finish();*/



                String nombreUsuario = txtUsuario.getText().toString();
                String pass = txtContraseña.getText().toString();

                txtErrorLogin.setVisibility(View.GONE);
                imgErrorUsuario.setVisibility(View.INVISIBLE);
                imgErrorContraseña.setVisibility(View.INVISIBLE);
                RetrofitCliente.getApiServicio().login(new LoginRequest(nombreUsuario, pass)).enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Usuario usuario = response.body();
                            // Login correcto, guardar sesión segura
                            SessionManager.getInstance(LoginActivity.this)
                                    .guardarSesion(usuario.getId(), usuario.getNombre(), usuario.getEmail());

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("nombre_usuario", usuario.getNombre());
                            startActivity(intent);
                            finish();
                            //Login incorrecto de usuario o contraseña poner el texto visible e iconos fuera
                        } else {
                            txtErrorLogin.setVisibility(View.VISIBLE);
                            imgErrorUsuario.setVisibility(View.VISIBLE);
                            imgErrorContraseña.setVisibility(View.VISIBLE);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Log.e("LOGIN_ERROR", t.getMessage(), t);
                        Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });

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
