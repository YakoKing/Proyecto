package com.playmatch.app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

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

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AjustesFragment extends Fragment {

    private TextView txtUserCorreo;
    private TextView tvUserName;
    private View btnEliminarCuenta, btnPerfil, btnContactoSupport , btnCambiarContraseña;
    private Button btnLogout;

    private boolean nuevaContraseñaVisible=false;

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
        btnContactoSupport = view.findViewById(R.id.btnContactoSupport);
        btnCambiarContraseña=view.findViewById(R.id.btnCambiarContraseña);


        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        txtUserCorreo.setText(sessionManager.getEmail());
        tvUserName.setText(sessionManager.getNombre());

        //Boton cerrar sesion
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.cerrarSesion();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        //Boton cambiar contraseña
        btnCambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogo=LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_cambiar_contrasena , null);
                EditText etNuevaContraseña= dialogo.findViewById(R.id.etNuevaContraseña);
                ImageView btnVerContraseñaNueva=dialogo.findViewById(R.id.btnVerContraseñaNueva);
                Button btnGuardarContraseñaNueva=dialogo.findViewById(R.id.btnGuardarContraseñaNueva);

                final AlertDialog dialog = new AlertDialog.Builder(requireContext()).setTitle("Cambiar contraseña")
                        .setView(dialogo).setNegativeButton("Cancelar" , null).create();

                //Mostar/ocultar contraseña en el dialogo
                btnVerContraseñaNueva.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nuevaContraseñaVisible){
                            etNuevaContraseña.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            btnVerContraseñaNueva.setImageResource(R.drawable.mostrar_contrasena);
                            nuevaContraseñaVisible=false;
                        }else{
                            etNuevaContraseña.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            btnVerContraseñaNueva.setImageResource(R.drawable.ocultar_contrasena);
                            nuevaContraseñaVisible=true;
                        }
                        etNuevaContraseña.setSelection(etNuevaContraseña.getText().length());
                    }
                });


                //Boton guardar nueva contraseña
                btnGuardarContraseñaNueva.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nuevaContraseña=etNuevaContraseña.getText().toString().trim();
                        if (nuevaContraseña.isEmpty()){
                            etNuevaContraseña.setError("Introduce una contraseña");
                            return;
                        }
                        //Reemplazar el usuario
                        Usuario usuario= new Usuario();
                        usuario.setNombre(sessionManager.getNombre());
                        usuario.setEmail(sessionManager.getEmail());
                        usuario.setTelefono(sessionManager.getTelefono());
                        usuario.setPosicion(sessionManager.getPosicion());
                        usuario.setPassword(nuevaContraseña);

                        //Actualizar la nueva contraseña en bdd
                        RetrofitCliente.getApiServicio().actualizarUsuario(sessionManager.getUsuarioId() , usuario)
                                .enqueue(new Callback<Usuario>() {
                                    @Override
                                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                        if (response.isSuccessful()){
                                            dialog.dismiss();
                                            Toast.makeText(requireContext() , "Contraseña actualizada" , Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(requireContext(), "Error al cambiar contraseña" , Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Usuario> call, Throwable t) {
                                        Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
                dialog.show();
            }



        });




        //Boton eliminar cuenta
        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext()).setTitle("Eliminar cuenta")
                        .setMessage("¿Estas seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer")
                        .setPositiveButton("Eliminar" , (dialog, which) -> {
                            int id = sessionManager.getUsuarioId();

                            RetrofitCliente.getApiServicio().eliminarUsuario(id).enqueue(new Callback<Map<String, String>>() {
                                @Override
                                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                                    if (response.isSuccessful()){
                                        sessionManager.cerrarSesion();
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

        //Boton soporte
        btnContactoSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String asunto = "Soporte PlayMatch";
                Intent intent = new Intent(Intent.ACTION_SENDTO);              //uri.encode()sirve para convertir espaciios y caracteres especiales automaticamente
                intent.setData(android.net.Uri.parse("mailto:ivan14rg@hotmail.com?subject=" + android.net.Uri.encode(asunto)));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(requireContext(), "No tienes app de correo instalada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Al clicar en el perfil de ajustes, redirigir al fragment de perfil
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