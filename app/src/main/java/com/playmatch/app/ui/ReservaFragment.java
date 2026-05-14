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
import com.playmatch.app.ApiServicio.ReservaRequest;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
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
    private Button btnConfirmarReserva, btnCancelarReserva;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserva, container, false);

        txtNombrePistaReserva = view.findViewById(R.id.txtNombrePistaReserva);
        imgPistaReserva = view.findViewById(R.id.imgPistaReserva);
        etFecha = view.findViewById(R.id.etFecha);
        etHora = view.findViewById(R.id.etHora);
        autoCompleteTipo = view.findViewById(R.id.autoCompleteTipo);
        btnConfirmarReserva = view.findViewById(R.id.btnConfirmarReserva);
        btnCancelarReserva = view.findViewById(R.id.btnCancelarReserva);

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

        btnConfirmarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarReserva();
            }
        });

        return view;
    }

    private void confirmarReserva() {
        String fecha = etFecha.getText().toString();
        final String hora = etHora.getText().toString();

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        int usuarioId = SessionManager.getInstance(requireContext()).getUsuarioId();
        long pistaId = pista.getId();
        String horaFin = calcularHoraFin(hora);

        ReservaRequest request = new ReservaRequest(usuarioId, pistaId, fecha, hora, horaFin);

        RetrofitCliente.getApiServicio().crearReserva(request).enqueue(new Callback<Reserva>() {
            @Override
            public void onResponse(@NonNull Call<Reserva> call, @NonNull Response<Reserva> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Reserva realizada con éxito!", Toast.LENGTH_LONG).show();
                    volverAReservas();
                } else {
                    if (response.code() == 400) {
                        Toast.makeText(getContext(), "Ya tienes una reserva en esa fecha", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Error al reservar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Reserva> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void volverAReservas() {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity activity = (HomeActivity) getActivity();
            activity.getSupportFragmentManager().popBackStack();
            activity.findViewById(R.id.nav_reserva).performClick();
        }
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year1, int monthOfYear, int dayOfMonth) {
                        String fechaFormateada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                        etFecha.setText(fechaFormateada);
                    }
                }, year, month, day);
        
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void mostrarTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute1) {
                        etHora.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1));
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }
}
