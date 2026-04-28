package com.playmatch.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playmatch.app.R;


public class AjustesFragment extends Fragment {

    private TextView txtUserCorreo;
    private  TextView tvUserName;


    public AjustesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);

        txtUserCorreo=view.findViewById(R.id.txtUserCorreo);
        tvUserName=view.findViewById(R.id.tvUserName);

        SharedPreferences preferences=requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String email=preferences.getString("email","");
        String nombre=preferences.getString("nombre" , "");
        txtUserCorreo.setText(email);
        tvUserName.setText(nombre);

        return view;
    }
}