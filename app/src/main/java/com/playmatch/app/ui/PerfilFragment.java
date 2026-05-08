package com.playmatch.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.entity.Usuario;
import com.playmatch.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.bumptech.glide.Glide;


public class PerfilFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView txtNombreUsuario;
    private TextView txtReputacion;
    private TextView txtNombre;
    private EditText etNombre;
    private TextView txtEdad;
    private EditText etEdad;
    private TextView txtPosicion;
    private EditText etPosicion;
    private TextView txtEmail;
    private EditText etCorreo;
    private Button btnGuardar;
    private ImageButton btnCambiarAvatar;
    private Usuario usuarioActual;

    private EditText etTelefono;

    private static final String[] avatares = {
            "https://i.imgur.com/d2ON2K4.png",
            "https://i.imgur.com/yaKJe3S.png",
            "https://i.imgur.com/IS2sbuV.png",
            "https://i.imgur.com/ypjBhlR.png"
    };


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        txtNombre = view.findViewById(R.id.txtNombre);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        etEdad = view.findViewById(R.id.etEdad);
        etPosicion = view.findViewById(R.id.etPosicion);
        etCorreo = view.findViewById(R.id.etCorreo);
        txtNombreUsuario = view.findViewById(R.id.txtNombreUsuario);
        btnCambiarAvatar=view.findViewById(R.id.btnCambiarAvatar);
        imgAvatar=view.findViewById(R.id.imgAvatar);
        etTelefono=view.findViewById(R.id.etTelefono);

        //Uso de SessionManager para obtener datos de forma instantánea en local
        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        int usuarioId = sessionManager.getUsuarioId();

        //Rellenar datos locales inmediatamente para evitar el retardo visual
        etNombre.setText(sessionManager.getNombre());
        txtNombreUsuario.setText(sessionManager.getNombre());
        etCorreo.setText(sessionManager.getEmail());
        int edad = sessionManager.getEdad();
        if (edad > 0) {
            etEdad.setText(String.valueOf(edad));
        }
        if (sessionManager.getPosicion() != null) {
            etPosicion.setText(sessionManager.getPosicion());
        }

        String avatarUrl=sessionManager.getAvatar();
        if (avatarUrl!=null && !avatarUrl.isEmpty()){
            Glide.with(this).load(avatarUrl).into(imgAvatar);
        }

        if (sessionManager.getTelefono()!=null){
            etTelefono.setText(sessionManager.getTelefono());
        }


        if (usuarioId != -1) {
            cargarDatosUsuario(usuarioId);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                txtNombreUsuario.setText(nombre);
            }
        });

        btnCambiarAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSeleccionAvatar();
            }
        });

        return view;
    }

    private void mostrarSeleccionAvatar(){

        //infflar layout para convertir el xml en objeto View

        View dialogo=LayoutInflater.from(requireContext()).inflate(R.layout.selector_avatares , null);
        ImageView iv1=dialogo.findViewById(R.id.avatar1);
        ImageView iv2=dialogo.findViewById(R.id.avatar2);
        ImageView iv3=dialogo.findViewById(R.id.avatar3);
        ImageView iv4=dialogo.findViewById(R.id.avatar4);

        //glide para descargarr la imagen de internet y meterla en su campo correspondiente
        Glide.with(this).load(avatares[0]).into(iv1);
        Glide.with(this).load(avatares[1]).into(iv2);
        Glide.with(this).load(avatares[2]).into(iv3);
        Glide.with(this).load(avatares[3]).into(iv4);

        //crrear dialogo y los metodos de cada imagen
        AlertDialog dialog=new AlertDialog.Builder(requireContext()).setTitle("Eligue tu avatar").setView(dialogo)
                .setNegativeButton("Cancelar" , null).create();

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar(avatares[0]); dialog.dismiss();
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar(avatares[1]); dialog.dismiss();
            }
        });
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar(avatares[2]); dialog.dismiss();
            }
        });
        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar(avatares[3]); dialog.dismiss();
            }
        });

        dialog.show();
    }

    //Interractuar con la bdd con Retrofit
    private void guardarAvatar(String url) {
        if (usuarioActual == null) return;
        usuarioActual.setAvatarUrl(url);
        int id = SessionManager.getInstance(requireContext()).getUsuarioId();
        RetrofitCliente.getApiServicio().actualizarUsuario(id, usuarioActual).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                //Si todo va bien cargamos la foto de la bdd en el elemento con glide
                if (response.isSuccessful() && getContext() != null) {
                    Glide.with(requireContext()).load(url).into(imgAvatar);
                //Obtenemos la sesion guardada en local y SessionManager lo guarda en el movil para no esperar la respuesta del servidor
                    SessionManager.getInstance(requireActivity()).guardarSesion(
                            usuarioActual.getId(),
                            usuarioActual.getNombre(),
                            usuarioActual.getEmail(),
                            usuarioActual.getEdad(),
                            usuarioActual.getPosicion(),
                            usuarioActual.getAvatarUrl(),
                            usuarioActual.getTelefono()
                    );
                    Toast.makeText(getContext(), "Avatar actualizado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Error al guardar avatar", Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void cargarDatosUsuario(int id) {
        RetrofitCliente.getApiServicio().getUsuario(id).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();
                    //nombre y correo  campos obligatorios
                    etNombre.setText(usuarioActual.getNombre());
                    txtNombreUsuario.setText(usuarioActual.getNombre());
                    etCorreo.setText(usuarioActual.getEmail());

                    if (usuarioActual.getEdad() > 0) {
                        etEdad.setText(String.valueOf(usuarioActual.getEdad()));
                    }
                    if (usuarioActual.getPosicion() != null && !usuarioActual.getPosicion().isEmpty()) {
                        etPosicion.setText(usuarioActual.getPosicion());
                    }
                    if (usuarioActual.getAvatarUrl() != null && !usuarioActual.getAvatarUrl().isEmpty()) {
                        Glide.with(requireContext()).load(usuarioActual.getAvatarUrl()).into(imgAvatar);
                    }
                    if (usuarioActual.getTelefono() != null && !usuarioActual.getTelefono().isEmpty()) {
                        etTelefono.setText(usuarioActual.getTelefono());

                    }

                    //Actualizar la sesión local con los datos frescos del servidor
                    if (getContext() != null) {
                        SessionManager.getInstance(getContext()).guardarSesion(
                                usuarioActual.getId(),
                                usuarioActual.getNombre(),
                                usuarioActual.getEmail(),
                                usuarioActual.getEdad(),
                                usuarioActual.getPosicion(),
                                usuarioActual.getAvatarUrl(),
                                usuarioActual.getTelefono()
                        );
                    }

                } else {
                    Log.e("PERFIL", "Error al cargar usuario: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("PERFIL", "Fallo de conexion: " + t.getMessage(), t);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error de conexion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
