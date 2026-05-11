package com.playmatch.app.ui;

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
        txtReputacion = view.findViewById(R.id.txtReputacion);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        // Cargar datos desde caché inmediatamente
        Usuario cachedUsuario = SessionManager.getInstance(requireContext()).getUsuario();
        if (cachedUsuario != null) {
            mostrarDatosUsuario(cachedUsuario);
        }

        // Refrescar desde el servidor
        int userId = SessionManager.getInstance(requireContext()).getUsuarioId();
        if (userId != -1) {
            cargarDatosUsuario(userId);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                txtNombreUsuario.setText(nombre);
            }
        });

        return view;
    }

    private void mostrarDatosUsuario(Usuario usuario) {
        if (usuario == null) return;
        etNombre.setText(usuario.getNombre());
        txtNombreUsuario.setText(usuario.getNombre());
        etCorreo.setText(usuario.getEmail());
        if (usuario.getEdad() > 0) {
            etEdad.setText(String.valueOf(usuario.getEdad()));
        }
        if (usuario.getPosicion() != null && !usuario.getPosicion().isEmpty()) {
            etPosicion.setText(usuario.getPosicion());
        }
        if (usuario.getReputacion() > 0) {
            txtReputacion.setText("Reputación: " + String.format("%.1f", usuario.getReputacion()));
        }
    }

    private void cargarDatosUsuario(int id) {
        RetrofitCliente.getApiServicio().getUsuario(id).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    
                    // Actualizar UI con datos frescos
                    mostrarDatosUsuario(usuario);

                    // Actualizar la sesión local con los datos frescos del servidor
                    if (getContext() != null) {
                        SessionManager.getInstance(getContext()).guardarSesion(usuario);
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
