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
import com.playmatch.app.adaptadores.PartidosAdapter;
import com.playmatch.app.entity.Partido;

import com.playmatch.app.utils.SessionManager;

import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartidosFragment extends Fragment {

    private RecyclerView recyclerPartidos;
    private TextView txtSinPartidos;

    public PartidosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partidos, container, false);

        recyclerPartidos = view.findViewById(R.id.recyclerPartidos);
        txtSinPartidos = view.findViewById(R.id.txtSinPartidos);

        recyclerPartidos.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarPartidosPublicos();

        return view;
    }

    private void cargarPartidosPublicos() {
        final int userIdActual = SessionManager.getInstance(requireContext()).getUsuarioId();
        RetrofitCliente.getApiServicio().getPartidosPublicos().enqueue(new Callback<List<Partido>>() {
            @Override
            public void onResponse(@NonNull Call<List<Partido>> call, @NonNull Response<List<Partido>> response) {
                if (!isAdded() || getView() == null) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Partido> lista = response.body();
                    
                    // Ordenar: Mis partidos primero
                    lista.sort(new Comparator<Partido>() {
                        @Override
                        public int compare(Partido p1, Partido p2) {
                            boolean p1EsMio = false;
                            if (p1.getReserva() != null && p1.getReserva().getUsuario() != null) {
                                p1EsMio = (p1.getReserva().getUsuario().getId() == userIdActual);
                            } else if (p1.getUsuario() != null) {
                                p1EsMio = (p1.getUsuario().getId() == userIdActual);
                            }

                            boolean p2EsMio = false;
                            if (p2.getReserva() != null && p2.getReserva().getUsuario() != null) {
                                p2EsMio = (p2.getReserva().getUsuario().getId() == userIdActual);
                            } else if (p2.getUsuario() != null) {
                                p2EsMio = (p2.getUsuario().getId() == userIdActual);
                            }

                            if (p1EsMio && !p2EsMio) return -1;
                            if (!p1EsMio && p2EsMio) return 1;
                            return 0;
                        }
                    });

                    if (lista.isEmpty()) {
                        txtSinPartidos.setVisibility(View.VISIBLE);
                        recyclerPartidos.setVisibility(View.GONE);
                    } else {
                        txtSinPartidos.setVisibility(View.GONE);
                        recyclerPartidos.setVisibility(View.VISIBLE);
                        PartidosAdapter adapter = new PartidosAdapter(lista);
                        recyclerPartidos.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Partido>> call, @NonNull Throwable t) {
                Log.e("PARTIDOS", "Error al cargar partidos: " + t.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}