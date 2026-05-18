package com.playmatch.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.adaptadores.ReservasAdapter;
import com.playmatch.app.entity.Reserva;
import com.playmatch.app.utils.SessionManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisReservasFragment extends Fragment {

    private RecyclerView recyclerReservas;
    private TextView txtVacio;

    public MisReservasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_reservas, container, false);

        recyclerReservas = view.findViewById(R.id.recyclerReservas);
        txtVacio = view.findViewById(R.id.txtVacio);

        recyclerReservas.setLayoutManager(new LinearLayoutManager(getContext()));

        int usuarioId = SessionManager.getInstance(requireContext()).getUsuarioId();
        if (usuarioId != -1) {
            cargarReservas(usuarioId);
        }

        return view;
    }

    private void cargarReservas(int usuarioId) {
        RetrofitCliente.getApiServicio().getReservasUsuario(usuarioId).enqueue(new Callback<List<Reserva>>() {
            @Override
            public void onResponse(@NonNull Call<List<Reserva>> call, @NonNull Response<List<Reserva>> response) {
                // Seguridad: Comprobar si el fragmento sigue activo antes de tocar la UI
                if (!isAdded() || getView() == null) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Reserva> lista = response.body();
                    
                    Collections.sort(lista, new Comparator<Reserva>() {
                        @Override
                        public int compare(Reserva r1, Reserva r2) {
                            int fechaComp = r1.getFechaPartido().compareTo(r2.getFechaPartido());
                            if (fechaComp != 0) return fechaComp;
                            return r1.getHoraInicio().compareTo(r2.getHoraInicio());
                        }
                    });

                    if (lista.isEmpty()) {
                        txtVacio.setVisibility(View.VISIBLE);
                        recyclerReservas.setVisibility(View.GONE);
                    } else {
                        txtVacio.setVisibility(View.GONE);
                        recyclerReservas.setVisibility(View.VISIBLE);
                        ReservasAdapter adapter = new ReservasAdapter(lista);
                        recyclerReservas.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Reserva>> call, @NonNull Throwable t) {
                Log.e("RESERVAS", "Error al cargar reservas: " + t.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al cargar tus reservas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}