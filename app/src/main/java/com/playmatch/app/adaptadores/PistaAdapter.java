package com.playmatch.app.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.playmatch.app.R;
import com.playmatch.app.entity.Pista;
import com.bumptech.glide.Glide;

import java.util.List;

public class PistaAdapter extends RecyclerView.Adapter<PistaAdapter.PistaViewHolder> {

    private List<Pista> listaPistas;

    public PistaAdapter(List<Pista> listaPistas) {
        this.listaPistas = listaPistas;
    }

    @NonNull
    @Override
    public PistaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_pista, parent, false);
        return new PistaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PistaViewHolder holder, int position) {
        Pista pista = listaPistas.get(position);

        holder.txtNombrePista.setText(pista.getNombre());
        holder.txtUbicacionPista.setText(pista.getUbicacion());
        holder.txtPrecioPista.setText("Precio: " + pista.getPrecioHora() + "€/hora");
        holder.txtCapacidadPista.setText("Capacidad máxima: " + pista.getCapacidadMax() + " jugadores");

        //si la foto no es null ni esta vacia Glide reemplaza la foto ejemplo por la de la url
        if (pista.getFoto() !=null && !pista.getFoto().isEmpty()){
            Glide.with(holder.itemView.getContext()).load(pista.getFoto()).centerCrop()
                    .placeholder(R.drawable.pista_ejemplo).into(holder.imgPista);
        }else{
            holder.imgPista.setImageResource(R.drawable.pista_ejemplo);
        }

        holder.btnReservar.setOnClickListener(v -> {
            //logica de reserva
        });
    }

    @Override
    public int getItemCount() {
        return listaPistas.size();
    }

    public static class PistaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPista;
        TextView txtNombrePista, txtUbicacionPista, txtPrecioPista, txtCapacidadPista;
        Button btnReservar;

        public PistaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPista = itemView.findViewById(R.id.imgPista);
            txtNombrePista = itemView.findViewById(R.id.txtNombrePista);
            txtUbicacionPista = itemView.findViewById(R.id.txtUbicacionPista);
            txtPrecioPista = itemView.findViewById(R.id.txtPrecioPista);
            txtCapacidadPista = itemView.findViewById(R.id.txtCapacidadPista);
            btnReservar = itemView.findViewById(R.id.btnReservar);
        }
    }
}
