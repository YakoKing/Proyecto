package com.playmatch.app.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.playmatch.app.ApiServicio.PartidoRequest;
import com.playmatch.app.ApiServicio.ReservaRequest;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.entity.Partido;
import com.playmatch.app.entity.Pista;
import com.playmatch.app.entity.Reserva;
import com.playmatch.app.utils.SessionManager;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservaFragment extends Fragment {

    private Pista pista;
    private TextView txtNombrePistaReserva;
    private ImageView imgPistaReserva;
    private EditText etFecha, etHora;
    private AutoCompleteTextView autoCompleteTipo;
    private Button btnConfirmarAccion, btnCancelarReserva;
    private MaterialSwitch switchPublicar;

    public ReservaFragment() {
    }

    public static ReservaFragment newInstance(Pista pista) {
        ReservaFragment fragment = new ReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable("pista", pista);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pista = (Pista) getArguments().getSerializable("pista");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserva, container, false);

        txtNombrePistaReserva = view.findViewById(R.id.txtNombrePistaReserva);
        imgPistaReserva = view.findViewById(R.id.imgPistaReserva);
        etFecha = view.findViewById(R.id.etFecha);
        etHora = view.findViewById(R.id.etHora);
        autoCompleteTipo = view.findViewById(R.id.autoCompleteTipo);
        btnConfirmarAccion = view.findViewById(R.id.btnConfirmarReserva);
        btnCancelarReserva = view.findViewById(R.id.btnCancelarReserva);
        switchPublicar = view.findViewById(R.id.switchPublicar);

        if (pista != null) {
            txtNombrePistaReserva.setText(pista.getNombre());
            if (pista.getFoto() != null && !pista.getFoto().isEmpty()) {
                Glide.with(this).load(pista.getFoto()).centerCrop()
                        .placeholder(R.drawable.pista_ejemplo).into(imgPistaReserva);
            } else {
                imgPistaReserva.setImageResource(R.drawable.pista_ejemplo);
            }
        }

        String[] tipos = {"Amistoso", "Competitivo", "Torneo"};
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tipos);
            autoCompleteTipo.setAdapter(adapter);
        }

        switchPublicar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnConfirmarAccion.setText("Publicar Partido");
                } else {
                    btnConfirmarAccion.setText("Confirmar Reserva");
                }
            }
        });

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        etHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarTimePicker();
            }
        });

        btnCancelarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof HomeActivity) {
                    HomeActivity activity = (HomeActivity) getActivity();
                    activity.getSupportFragmentManager().popBackStack();
                    activity.findViewById(R.id.recyclerPistas).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.contenedorFragments).setVisibility(View.GONE);
                    activity.findViewById(R.id.BarTop).setVisibility(View.VISIBLE);
                }
            }
        });

        btnConfirmarAccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Siempre ejecutamos reserva primero por integridad de la DB (Foreign Key reserva_id)
                ejecutarFlujoReserva();
            }
        });

        return view;
    }

    private void ejecutarFlujoReserva() {
        final String fecha = etFecha.getText().toString();
        final String hora = etHora.getText().toString();
        final String nivel = autoCompleteTipo.getText().toString();

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        int usuarioId = SessionManager.getInstance(requireContext()).getUsuarioId();
        ReservaRequest request = new ReservaRequest(usuarioId, pista.getId(), fecha, hora, calcularHoraFin(hora));

        RetrofitCliente.getApiServicio().crearReserva(request).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(@NonNull Call<Reserva> call, @NonNull Response<Reserva> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int reservaId = response.body().getId();
                    
                    if (switchPublicar.isChecked()) {
                        //ID de la reserva recien creada, si es para publicar
                        publicarPartidoReal(reservaId, nivel);
                    } else {
                        Toast.makeText(getContext(), "Reserva privada confirmada", Toast.LENGTH_SHORT).show();
                        irAMisReservas();
                    }
                } else {
                    Toast.makeText(getContext(), "Error: Ya hay una reserva en esa fecha", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Reserva> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo de red al reservar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void publicarPartidoReal(int reservaId, String nivel) {
        // Aseguramos enviar los campos que el servidor espera
        PartidoRequest request = new PartidoRequest(reservaId, "Partido " + nivel, pista.getCapacidadMax(), true, "abierto", nivel);
        
        RetrofitCliente.getApiServicio().crearPartido(request).enqueue(new Callback<Partido>() {
            @Override
            public void onResponse(@NonNull Call<Partido> call, @NonNull Response<Partido> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Partido publicado con éxito!", Toast.LENGTH_LONG).show();
                    irAPartidos();
                } else {
                    Toast.makeText(getContext(), "Reserva hecha, pero error al publicar: " + response.code(), Toast.LENGTH_SHORT).show();
                    irAPartidos();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Partido> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Reserva hecha, pero fallo al publicar el partido", Toast.LENGTH_SHORT).show();
                irAPartidos();
            }
        });
    }

    private void irAPartidos() {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity activity = (HomeActivity) getActivity();
            activity.getSupportFragmentManager().popBackStack();
            activity.findViewById(R.id.nav_partidos).performClick();
        }
    }

    private void irAMisReservas() {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity activity = (HomeActivity) getActivity();
            activity.getSupportFragmentManager().popBackStack();
            activity.findViewById(R.id.nav_reserva).performClick();
        }
    }

    private String calcularHoraFin(String horaInicio) {
        try {
            String[] partes = horaInicio.split(":");
            int hora = Integer.parseInt(partes[0]);
            int minuto = Integer.parseInt(partes[1]);
            hora = (hora + 1) % 24;
            return String.format(Locale.getDefault(), "%02d:%02d", hora, minuto);
        } catch (Exception e) {
            return horaInicio;
        }
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year1, int monthOfYear, int dayOfMonth) {
                        etFecha.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void mostrarTimePicker() {
        final Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute1) {
                        etHora.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1));
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }
}
