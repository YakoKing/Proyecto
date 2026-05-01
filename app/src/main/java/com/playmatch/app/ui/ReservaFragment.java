package com.playmatch.app.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.playmatch.app.R;
import com.playmatch.app.entity.Pista;

import java.util.Calendar;
import java.util.Locale;

public class ReservaFragment extends Fragment {

    private Pista pista;
    private TextView txtNombrePistaReserva;
    private EditText etFecha, etHora;
    private Spinner spinnerTipo;
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
        etFecha = view.findViewById(R.id.etFecha);
        etHora = view.findViewById(R.id.etHora);
        spinnerTipo = view.findViewById(R.id.spinnerTipo);
        btnConfirmarReserva = view.findViewById(R.id.btnConfirmarReserva);
        btnCancelarReserva = view.findViewById(R.id.btnCancelarReserva);

        if (pista != null) {
            txtNombrePistaReserva.setText(pista.getNombre());
        }

        // Configurar Spinner
        String[] tipos = {"Amistoso", "Competitivo", "Torneo"};
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tipos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipo.setAdapter(adapter);
        }

        // Date Picker
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        // Time Picker
        etHora.setOnClickListener(v -> mostrarTimePicker());

        btnCancelarReserva.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnConfirmarReserva.setOnClickListener(v -> {
            // Aquí irá la lógica de llamada a la API en el futuro
            Toast.makeText(getContext(), "Reserva solicitada para " + pista.getNombre(), Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> etFecha.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1),
                year, month, day);
        datePickerDialog.show();
    }

    private void mostrarTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute1) -> etHora.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1)),
                hour, minute, true);
        timePickerDialog.show();
    }
}
