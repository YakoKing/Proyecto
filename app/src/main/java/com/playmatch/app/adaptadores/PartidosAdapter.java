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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.entity.Partido;
import com.playmatch.app.entity.Pista;
import com.playmatch.app.entity.Usuario;
import com.playmatch.app.utils.SessionManager;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartidosAdapter extends RecyclerView.Adapter<PartidosAdapter.PartidoViewHolder> {

    private List<Partido> listaPartidos;

    public PartidosAdapter(List<Partido> listaPartidos) {
        this.listaPartidos = listaPartidos;
    }

    @NonNull
    @Override
    public PartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_partido, parent, false);
        return new PartidoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull final PartidoViewHolder holder, int position) {
        final Partido partido = listaPartidos.get(position);
        final Context context = holder.itemView.getContext();
        final int userIdActual = SessionManager.getInstance(context).getUsuarioId();
        final String nombreActual = SessionManager.getInstance(context).getNombre();

        // 1. IDENTIFICAR AL DUEÑO
        boolean esMio = false;
        Usuario organizadorObj = null;
        String nombreOrganizadorStr = "Desconocido";

        if (partido.getUsuario() != null) {
            organizadorObj = partido.getUsuario();
            esMio = (organizadorObj.getId() == userIdActual);
            nombreOrganizadorStr = organizadorObj.getNombre();
        } 
        else if (partido.getReserva() != null && partido.getReserva().getUsuario() != null) {
            organizadorObj = partido.getReserva().getUsuario();
            esMio = (organizadorObj.getId() == userIdActual);
            nombreOrganizadorStr = organizadorObj.getNombre();
        }
        else if (partido.getOrganizador() != null && !partido.getOrganizador().isEmpty()) {
            nombreOrganizadorStr = partido.getOrganizador();
            if (nombreOrganizadorStr.equalsIgnoreCase(nombreActual)) {
                esMio = true;
            }
        }
        else if (partido.getReserva() != null && partido.getReserva().getUsuarioId() == userIdActual) {
            esMio = true;
            nombreOrganizadorStr = nombreActual;
        }

        // 2. DISEÑO DIFERENCIADO Y BOTONES
        if (esMio) {
            holder.cardView.setStrokeColor(android.graphics.Color.parseColor("#FFFF00")); // Amarillo
            holder.cardView.setStrokeWidth(4);
            holder.cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#2A2A10"));
            holder.txtNombreOrganizador.setText("Organizado por mí");
            
            holder.btnUnirme.setVisibility(View.GONE);
            holder.btnBorrar.setVisibility(View.VISIBLE);
        } else {
            holder.cardView.setStrokeColor(android.graphics.Color.parseColor("#00FF9C"));
            holder.cardView.setStrokeWidth(1);
            holder.cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"));
            holder.txtNombreOrganizador.setText("Organizado por " + nombreOrganizadorStr);
            
            holder.btnUnirme.setVisibility(View.VISIBLE);
            holder.btnBorrar.setVisibility(View.GONE);
        }

        // 3. REPUTACIÓN Y AVATAR
        if (organizadorObj != null) {
            holder.txtReputacionOrganizador.setText("Reputación: " + String.format("%.1f", organizadorObj.getReputacion()) + " ★");
            if (organizadorObj.getAvatarUrl() != null && !organizadorObj.getAvatarUrl().isEmpty()) {
                Glide.with(context).load(organizadorObj.getAvatarUrl()).into(holder.imgOrganizador);
            } else {
                holder.imgOrganizador.setImageResource(R.drawable.perfil);
            }
        } else {
            holder.txtReputacionOrganizador.setText("Reputación: -- ★");
            holder.imgOrganizador.setImageResource(R.drawable.perfil);
        }

        // 4. DATOS DE LA PISTA
        Pista pistaObj = null;
        String infoFecha = "Fecha no disponible";

        if (partido.getReserva() != null) {
            pistaObj = partido.getReserva().getPista();
            infoFecha = partido.getReserva().getFechaPartido() + ", " + partido.getReserva().getHoraInicio();
        } else if (partido.getPista() != null) {
            holder.txtNombrePistaPartido.setText(partido.getPista());
            infoFecha = (partido.getFecha() != null ? partido.getFecha() : "") + ", " + 
                        (partido.getHoraInicio() != null ? partido.getHoraInicio() : "");
        }

        if (pistaObj != null) {
            holder.txtNombrePistaPartido.setText(pistaObj.getNombre());
            if (pistaObj.getFoto() != null && !pistaObj.getFoto().isEmpty()) {
                Glide.with(context).load(pistaObj.getFoto()).centerCrop()
                        .placeholder(R.drawable.pista_ejemplo).into(holder.imgPistaPartido);
            } else {
                holder.imgPistaPartido.setImageResource(R.drawable.pista_ejemplo);
            }
            holder.txtFechaHoraPartido.setText(infoFecha);
        } else {
            if (holder.txtNombrePistaPartido.getText().toString().equals("Pista")) {
                holder.txtNombrePistaPartido.setText("Pista no asignada");
            }
            holder.imgPistaPartido.setImageResource(R.drawable.pista_ejemplo);
            holder.txtFechaHoraPartido.setText(infoFecha);
        }

        // 5. CONTEO DE PLAZAS
        int max = partido.getJugadoresMax();
        int actuales = 1; 
        int faltan = max - actuales;

        holder.txtPlazas.setText("Faltan " + (faltan > 0 ? faltan : 0) + " jugadores (" + actuales + "/" + max + ")");
        
        if (partido.getEstado() != null) {
            holder.txtNivelPartido.setText(partido.getEstado().toUpperCase());
        } else {
            holder.txtNivelPartido.setText("ABIERTO");
        }

        // 6. LISTENERS
        holder.btnUnirme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Solicitud enviada para unirse", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int posActual = holder.getAdapterPosition();
                if (posActual != RecyclerView.NO_POSITION) {
                    mostrarConfirmacionBorrado(context, partido, posActual);
                }
            }
        });
    }

    private void mostrarConfirmacionBorrado(final Context context, final Partido partido, final int position) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Borrar publicación")
                .setMessage("¿Estás seguro de que quieres eliminar este partido y cancelar su reserva?")
                .setPositiveButton("Eliminar Todo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarPartidoEnCascada(context, partido, position);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarPartidoEnCascada(final Context context, final Partido partido, final int position) {
        // Borrar el Partido público
        RetrofitCliente.getApiServicio().eliminarPartido((long) partido.getId()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    // PASO 2: Borrar la Reserva asociada
                    Long resId = null;
                    if (partido.getReservaId() != null) {
                        resId = (long) partido.getReservaId();
                    } else if (partido.getReserva() != null) {
                        resId = (long) partido.getReserva().getId();
                    }

                    if (resId != null) {
                        RetrofitCliente.getApiServicio().eliminarReserva(resId).enqueue(new Callback<Map<String, String>>() {
                            @Override
                            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> res) {
                                Toast.makeText(context, "Publicación y reserva canceladas", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(Call<Map<String, String>> call, Throwable t) {}
                        });
                    } else {
                        Toast.makeText(context, "Partido eliminado", Toast.LENGTH_SHORT).show();
                    }

                    listaPartidos.remove(position);
                    notifyItemRemoved(position);
                } else {
                    Toast.makeText(context, "Error al eliminar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPartidos.size();
    }

    public static class PartidoViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.card.MaterialCardView cardView;
        ImageView imgOrganizador, imgPistaPartido;
        TextView txtNombreOrganizador, txtReputacionOrganizador, txtNivelPartido, txtNombrePistaPartido, txtFechaHoraPartido, txtPlazas;
        Button btnUnirme, btnBorrar;

        public PartidoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardPartido);
            imgOrganizador = itemView.findViewById(R.id.imgOrganizador);
            imgPistaPartido = itemView.findViewById(R.id.imgPistaPartido);
            txtNombreOrganizador = itemView.findViewById(R.id.txtNombreOrganizador);
            txtReputacionOrganizador = itemView.findViewById(R.id.txtReputacionOrganizador);
            txtNivelPartido = itemView.findViewById(R.id.txtNivelPartido);
            txtNombrePistaPartido = itemView.findViewById(R.id.txtNombrePistaPartido);
            txtFechaHoraPartido = itemView.findViewById(R.id.txtFechaHoraPartido);
            txtPlazas = itemView.findViewById(R.id.txtPlazas);
            btnUnirme = itemView.findViewById(R.id.btnUnirme);
            btnBorrar = itemView.findViewById(R.id.btnBorrarPartido);
        }
    }
}
