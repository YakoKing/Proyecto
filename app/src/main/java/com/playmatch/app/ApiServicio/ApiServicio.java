package com.playmatch.app.ApiServicio;

import com.playmatch.app.entity.Participacion;
import com.playmatch.app.entity.Partido;
import com.playmatch.app.entity.Pista;
import com.playmatch.app.entity.Reserva;
import com.playmatch.app.entity.Usuario;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServicio {

    // USUARIOS
    @POST("usuarios/login")
    Call<Usuario> login(@Body LoginRequest loginRequest);

    @GET("usuarios")
    Call<List<Usuario>> getUsuarios();

    @GET("usuarios/{id}")
    Call<Usuario> getUsuario(@Path("id") long id);

    @POST("usuarios")
    Call<Usuario> registrar(@Body Usuario usuario);

    @PUT("usuarios/{id}")
    Call<Usuario> actualizarUsuario(@Path("id") long id, @Body Usuario usuario);

    @DELETE("usuarios/{id}")
    Call<Map<String, String>> eliminarUsuario(@Path("id") long id);

    // PISTAS
    @GET("pistas")
    Call<List<Pista>> getPistas();

    @GET("pistas/{id}")
    Call<Pista> getPista(@Path("id") long id);

    @GET("pistas/buscar")
    Call<List<Pista>> buscarPistas(@Query("ubicacion") String ubicacion);

    // RESERVAS
    @GET("reservas")
    Call<List<Reserva>> getReservas();

    @GET("reservas/usuario/{id}")
    Call<List<Reserva>> getReservasUsuario(@Path("id") long usuarioId);

    @POST("reservas")
    Call<Reserva> crearReserva(@Body ReservaRequest request);

    @PUT("reservas/{id}/estado")
    Call<Reserva> actualizarEstadoReserva(@Path("id") long id, @Body Map<String, String> estado);

    @DELETE("reservas/{id}")
    Call<Map<String, String>> eliminarReserva(@Path("id") long id);

    // PARTIDOS
    @GET("partidos")
    Call<List<Partido>> getPartidos();

    @GET("partidos/publicos")
    Call<List<Partido>> getPartidosPublicos();

    @GET("partidos/{id}")
    Call<Partido> getPartido(@Path("id") long id);

    @POST("partidos")
    Call<Partido> crearPartido(@Body PartidoRequest request);

    @PUT("partidos/{id}/estado")
    Call<Partido> actualizarEstadoPartido(@Path("id") long id, @Body Map<String, String> estado);

    @DELETE("partidos/{id}")
    Call<Map<String, String>> eliminarPartido(@Path("id") long id);

    // PARTICIPACIONES
    @GET("participaciones/partido/{id}")
    Call<List<Participacion>> getParticipacionesPartido(@Path("id") long partidoId);

    @GET("participaciones/usuario/{id}")
    Call<List<Participacion>> getParticipacionesUsuario(@Path("id") long usuarioId);

    @POST("participaciones")
    Call<Participacion> unirseAPartido(@Body ParticipacionRequest request);

    @DELETE("participaciones/salir")
    Call<Map<String, String>> salirDePartido(@Query("usuarioId") long usuarioId, @Query("partidoId") long partidoId);
}
