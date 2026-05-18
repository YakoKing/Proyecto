package com.playmatch.app.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private EditText etNombre;
    private EditText etEdad;
    private EditText etPosicion;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar vistas
        etNombre = view.findViewById(R.id.etNombre);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        etEdad = view.findViewById(R.id.etEdad);
        etPosicion = view.findViewById(R.id.etPosicion);
        etCorreo = view.findViewById(R.id.etCorreo);
        txtNombreUsuario = view.findViewById(R.id.txtNombreUsuario);
        txtReputacion = view.findViewById(R.id.txtReputacion);
        btnCambiarAvatar = view.findViewById(R.id.btnCambiarAvatar);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        etTelefono = view.findViewById(R.id.etTelefono);

        // Cargar datos desde caché inmediatamente
        Usuario cachedUsuario = SessionManager.getInstance(requireContext()).getUsuario();
        if (cachedUsuario != null) {
            usuarioActual = cachedUsuario;
            mostrarDatosUsuario(cachedUsuario);
        }

        // Refrescar desde el servidor para tener datos actualizados
        int userId = SessionManager.getInstance(requireContext()).getUsuarioId();
        if (userId != -1) {
            cargarDatosUsuario(userId);
        }

        // Boton guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usuarioActual == null) return;

                String nombre = etNombre.getText().toString();
                String correo = etCorreo.getText().toString();
                String posicion = etPosicion.getText().toString();
                String telefono = etTelefono.getText().toString();
                String edadStr = etEdad.getText().toString().trim();

                //asignar valores nuevos al usuario
                usuarioActual.setNombre(nombre);
                if (!correo.contains("@") || !correo.contains(".")){
                    etCorreo.setError("Formato de email no valido");
                    return;
                }
                usuarioActual.setEmail(correo);
                usuarioActual.setPosicion(posicion);
                usuarioActual.setTelefono(telefono);
                if (!edadStr.isEmpty()){
                    int edad=Integer.parseInt(edadStr);
                    if (edad<16 || edad>100){
                        etEdad.setError("Introduce una edad vÃ¡lida");
                        return;
                    }
                    usuarioActual.setEdad(Integer.parseInt(edadStr));
                }

                //obtener id del usuario logueado
                int id=SessionManager.getInstance(requireContext()).getUsuarioId();
                RetrofitCliente.getApiServicio().actualizarUsuario(id , usuarioActual).enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if(response.isSuccessful()){
                            //texto debajo del avatar
                            txtNombreUsuario.setText(nombre);
                            SessionManager.getInstance(requireContext()).getUsuario();
                            Toast.makeText(getContext(), "Perfil actualizado" , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        Toast.makeText(getContext(), "Error al guardar cambios" , Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void mostrarSeleccionAvatar() {
        if (!isAdded()) return;
        
        View dialogo = LayoutInflater.from(requireContext()).inflate(R.layout.selector_avatares, null);
        ImageView iv1 = dialogo.findViewById(R.id.avatar1);
        ImageView iv2 = dialogo.findViewById(R.id.avatar2);
        ImageView iv3 = dialogo.findViewById(R.id.avatar3);
        ImageView iv4 = dialogo.findViewById(R.id.avatar4);

        //glide para descargarr la imagen de internet y meterla en su campo correspondiente
        Glide.with(this).load(avatares[0]).into(iv1);
        Glide.with(this).load(avatares[1]).into(iv2);
        Glide.with(this).load(avatares[2]).into(iv3);
        Glide.with(this).load(avatares[3]).into(iv4);

        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Elige tu avatar")
                .setView(dialogo)
                .setNegativeButton("Cancelar", null)
                .create();

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar(avatares[0]);
                dialog.dismiss();
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar(avatares[1]);
                dialog.dismiss();
            }
        });
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar(avatares[2]);
                dialog.dismiss();
            }
        });
        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar(avatares[3]);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void guardarAvatar(String url) {
        if (usuarioActual == null) return;
        usuarioActual.setAvatarUrl(url);
        actualizarDatosUsuario();
    }

    private void actualizarDatosUsuario() {
        if (!isAdded()) return;
        
        int id = SessionManager.getInstance(requireContext()).getUsuarioId();
        RetrofitCliente.getApiServicio().actualizarUsuario(id, usuarioActual).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(@NonNull Call<Usuario> call, @NonNull Response<Usuario> response) {
                if (!isAdded() || getView() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();
                    mostrarDatosUsuario(usuarioActual);
                    SessionManager.getInstance(requireContext()).guardarSesion(usuarioActual);
                    Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Usuario> call, @NonNull Throwable t) {
                if (!isAdded() || getView() == null) return;
                Toast.makeText(getContext(), "Error al guardar cambios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosUsuario(int id) {
        if (!isAdded()) return;

        RetrofitCliente.getApiServicio().getUsuario(id).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(@NonNull Call<Usuario> call, @NonNull Response<Usuario> response) {
                if (!isAdded() || getView() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();
                    mostrarDatosUsuario(usuarioActual);
                    if (getContext() != null) {
                        SessionManager.getInstance(getContext()).guardarSesion(usuarioActual);
                    }
                } else {
                    Log.e("PERFIL", "Error al cargar usuario: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Usuario> call, @NonNull Throwable t) {
                if (!isAdded() || getView() == null) return;
                Log.e("PERFIL", "Fallo de conexion: " + t.getMessage());
            }
        });
    }

    private void mostrarDatosUsuario(Usuario usuario) {
        if (usuario == null || !isAdded() || getView() == null) return;

        etNombre.setText(usuario.getNombre());
        txtNombreUsuario.setText(usuario.getNombre());
        etCorreo.setText(usuario.getEmail());
        
        if (usuario.getEdad() > 0) {
            etEdad.setText(String.valueOf(usuario.getEdad()));
        } else {
            etEdad.setText("");
        }
        
        if (usuario.getPosicion() != null) {
            etPosicion.setText(usuario.getPosicion());
        }
        
        if (usuario.getTelefono() != null) {
            etTelefono.setText(usuario.getTelefono());
        }

        if (usuario.getReputacion() > 0 && txtReputacion != null) {
            txtReputacion.setText("Reputación: " + String.format("%.1f", usuario.getReputacion()));
        }

        if (usuario.getAvatarUrl() != null && !usuario.getAvatarUrl().isEmpty()) {
            Glide.with(this).load(usuario.getAvatarUrl()).into(imgAvatar);
        }
    }
}
