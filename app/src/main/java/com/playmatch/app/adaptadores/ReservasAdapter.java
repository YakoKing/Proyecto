package com.playmatch.app.adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.entity.Pista;
import com.playmatch.app.entity.Reserva;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder> {

    private List<Reserva> listaReservas;

    public ReservasAdapter(List<Reserva> listaReservas) {
        this.listaReservas = listaReservas;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_mis_reservas, parent, false);
        return new ReservaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        final Reserva reserva = listaReservas.get(position);
        final Context context = holder.itemView.getContext();

        if (reserva.getPista() != null) {
            Pista pista = reserva.getPista();
            holder.txtNombrePistaReserva.setText(pista.getNombre());

            if (pista.getFoto() != null && !pista.getFoto().isEmpty()) {
                Glide.with(context).load(pista.getFoto()).centerCrop()
                        .placeholder(R.drawable.pista_ejemplo).into(holder.imgPistaReserva);
            } else {
                holder.imgPistaReserva.setImageResource(R.drawable.pista_ejemplo);
            }
        } else {
            // Por si no viene el objeto pista (usando el ID plano)
            holder.txtNombrePistaReserva.setText("Reserva #" + reserva.getId());
            holder.imgPistaReserva.setImageResource(R.drawable.pista_ejemplo);
        }
        
        // Fecha y horario (en color blanco por el layout)
        holder.txtFechaReserva.setText("Fecha: " + reserva.getFechaPartido());
        holder.txtHorarioReserva.setText(reserva.getHoraInicio() + " - " + reserva.getHoraFin());

        // Configurar botón cancelar
        holder.btnCancelarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion(context, reserva, holder.getAdapterPosition());
            }
        });
    }

    private void mostrarDialogoConfirmacion(final Context context, final Reserva reserva, final int position) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("Confirmar cancelación")
                .setMessage("¿Estás seguro de que deseas cancelar esta reserva?")
                .setPositiveButton("Sí, cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarReservaServidor(context, reserva.getId(), position);
                    }
                })
                .setNegativeButton("No", null)
                .show();

        // Personalizar colores de los botones después de mostrar el diálogo
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.RED);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.BLACK);
        
        // Asegurar que el fondo del diálogo sea blanco para que se lea bien
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
    }

    private void eliminarReservaServidor(final Context context, int reservaId, final int position) {
        RetrofitCliente.getApiServicio().eliminarReserva(reservaId).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Reserva cancelada correctamente", Toast.LENGTH_SHORT).show();
                    listaReservas.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listaReservas.size());
                } else {
                    if (response.code() == 404) {
                        Toast.makeText(context, "Error: Esta reserva tiene un partido público. Bórralo primero.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "No se pudo cancelar la reserva (Error " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaReservas.size();
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPistaReserva;
        TextView txtNombrePistaReserva, txtFechaReserva, txtHorarioReserva;
        Button btnCancelarReserva;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPistaReserva = itemView.findViewById(R.id.imgPistaReserva);
            txtNombrePistaReserva = itemView.findViewById(R.id.txtNombrePistaReserva);
            txtFechaReserva = itemView.findViewById(R.id.txtFechaReserva);
            txtHorarioReserva = itemView.findViewById(R.id.txtHorarioReserva);
            btnCancelarReserva = itemView.findViewById(R.id.btnCancelarReserva);
        }
    }
}