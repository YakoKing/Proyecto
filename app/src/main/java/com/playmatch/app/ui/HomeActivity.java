package com.playmatch.app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.adaptadores.PistaAdapter;
import com.playmatch.app.entity.Partido;
import com.playmatch.app.entity.Pista;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgFondo;
    private MaterialToolbar BarTop;
    private BottomNavigationView BarMenu;
    private RecyclerView recyclerPistas;
    FrameLayout contenedorFragments;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgFondo=findViewById(R.id.imgFondo);
        BarMenu=findViewById(R.id.BarMenu);
        BarTop=findViewById(R.id.BarTop);
        RecyclerView recyclerPistas = findViewById(R.id.recyclerPistas);
        FrameLayout contenedorFragments = findViewById(R.id.contenedorFragments);

        //Conffigurar el recyclerviews
        recyclerPistas.setLayoutManager((new LinearLayoutManager(this)));

        //cargar las pistas desde la api

        RetrofitCliente.getApiServicio().getPistas().enqueue(new Callback<List<Pista>>() {
            @Override
            public void onResponse(Call<List<Pista>> call, Response<List<Pista>> response) {
                if (response.isSuccessful() && response.body() !=null){
                    PistaAdapter adapter= new PistaAdapter(response.body());
                    recyclerPistas.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Pista>> call, Throwable t) {
                Log.e("PISTAS" ,"Error al cargar las pistas " + t.getMessage());
            }
        });

        String nombreUsuario=getIntent().getStringExtra("nombre_usuario");
        BarTop.setTitle("Bienvenido " + nombreUsuario);


        //Fragments del menu
        BarMenu.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int seleccionMenu=item.getItemId();

                //Cuando estamos en inicio mostramos las pistas y ocultamos los ffragments
                if (seleccionMenu==R.id.nav_home){
                    recyclerPistas.setVisibility(View.VISIBLE);
                    contenedorFragments.setVisibility(View.GONE);
                    BarTop.setVisibility(View.VISIBLE);
                    return true;
                }
                //Si pulsamos el item de perfil o ajustes ponemos visible su fragment y ocultamos las pistas
                if (seleccionMenu==R.id.nav_perfil){
                    recyclerPistas.setVisibility(View.GONE);
                    contenedorFragments.setVisibility(View.VISIBLE);
                    BarTop.setVisibility(View.GONE);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenedorFragments, new PerfilFragment()).commit();
                    return true;
                }
                if (seleccionMenu==R.id.nav_ajustes){
                    recyclerPistas.setVisibility(View.GONE);
                    contenedorFragments.setVisibility(View.VISIBLE);
                    BarTop.setVisibility(View.GONE);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenedorFragments, new AjustesFragment()).commit();
                    return true;
                }

                return false;
            }
        });
    }
}
