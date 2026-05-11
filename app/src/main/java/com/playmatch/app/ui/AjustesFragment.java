package com.playmatch.app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.utils.SessionManager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AjustesFragment extends Fragment {

    private TextView txtUserCorreo;
    private TextView tvUserName;
    private View btnEliminarCuenta, btnPerfil;
    private Button btnLogout;

    public AjustesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);

        txtUserCorreo = view.findViewById(R.id.txtUserCorreo);
        tvUserName = view.findViewById(R.id.tvUserName);
        btnEliminarCuenta = view.findViewById(R.id.btnEliminarCuenta);
        btnPerfil = view.findViewById(R.id.btnPerfil);
        btnLogout = view.findViewById(R.id.btnLogout);

        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        txtUserCorreo.setText(sessionManager.getEmail());
        tvUserName.setText(sessionManager.getNombre());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.cerrarSesion();
                // Volver a la pantalla de Login
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Boton eliminar cuenta
        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext()).setTitle("Eliminar cuenta")
                        .setMessage("¿Estas seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer")
                        .setPositiveButton("Eliminar" , (dialog, which) -> {
                            int id = SessionManager.getInstance(requireContext()).getUsuarioId();

                            RetrofitCliente.getApiServicio().eliminarUsuario(id).enqueue(new Callback<Map<String, String>>() {
                                @Override
                                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                                    if (response.isSuccessful()){
                                        //si la peticion es correcta borramos user de la base de datos
                                        SessionManager.getInstance(requireContext()).cerrarSesion();
                                        //volvemos al login
                                        Intent intent=new Intent(requireActivity(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                                    Toast.makeText(requireActivity() , "Error al eliminar la cuenta" , Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancelar",null).show();

            }
        });

        // Al clicar en el perfil de ajustes, redirigir al fragment de perfil
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenedorFragments, new PerfilFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        return view;
    }
}