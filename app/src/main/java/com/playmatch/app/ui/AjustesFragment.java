package com.playmatch.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.playmatch.app.R;
import com.playmatch.app.utils.SessionManager;


public class AjustesFragment extends Fragment {
    private TextView txtUserCorreo;
    private TextView tvUserName;
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

        return view;
    }
}
