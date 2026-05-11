package com.playmatch.app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.Button;
import android.widget.EditText;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.playmatch.app.ApiServicio.RetrofitCliente;
import com.playmatch.app.R;
import com.playmatch.app.adaptadores.PistaAdapter;
import com.playmatch.app.entity.Pista;
import com.playmatch.app.entity.Usuario;
import com.playmatch.app.utils.SessionManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgFondo;
    private MaterialToolbar BarTop;
    private BottomNavigationView BarMenu;
    private RecyclerView recyclerPistas;
    private FrameLayout contenedorFragments;

    private List<Pista> listaOriginal;
    private List<Pista> listaFiltrada;
    private PistaAdapter adapter;

    // Estados de filtros
    private String queryActual = "";
    private String provinciaSeleccionada = "Todas";
    private int ordenSeleccionado = 0; // 0: Sin orden, 1: Precio Asc, 2: Precio Desc



    private View layoutFiltros;
    private android.widget.Spinner spinnerProvincia;
    private android.widget.RadioGroup rgOrden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgFondo = findViewById(R.id.imgFondo);
        BarMenu = findViewById(R.id.BarMenu);
        BarTop = findViewById(R.id.BarTop);
        recyclerPistas = findViewById(R.id.recyclerPistas);
        contenedorFragments = findViewById(R.id.contenedorFragments);
        layoutFiltros = findViewById(R.id.layoutFiltros);

        configurarVistaFiltros();

        // Configurar el recyclerPistas
        recyclerPistas.setLayoutManager((new LinearLayoutManager(this)));

        // Configurar el Toolbar como ActionBar para usar menús
        setSupportActionBar(BarTop);

        // Cargar las pistas desde la api
        RetrofitCliente.getApiServicio().getPistas().enqueue(new Callback<List<Pista>>() {
            @Override
            public void onResponse(Call<List<Pista>> call, Response<List<Pista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaOriginal = response.body();
                    listaFiltrada = new java.util.ArrayList<>(listaOriginal);
                    adapter = new PistaAdapter(listaFiltrada);
                    recyclerPistas.setAdapter(adapter);
                    // Aplicar filtros por si ya había algo escrito o seleccionado antes de cargar
                    aplicarFiltrosYOrden();
                }
            }

            @Override
            public void onFailure(Call<List<Pista>> call, Throwable t) {
                Log.e("PISTAS", "Error al cargar las pistas " + t.getMessage());
            }
        });

        String nombreUsuario = getIntent().getStringExtra("nombre_usuario");
        BarTop.setTitle("Bienvenido " + (nombreUsuario != null ? nombreUsuario : ""));

        // Pre-fetch de datos del usuario
        preFetchDatosUsuario();

        // Fragments del menu
        BarMenu.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int seleccionMenu = item.getItemId();

                // Siempre ocultar el panel de filtros al cambiar de pestaña
                if (layoutFiltros != null) {
                    layoutFiltros.setVisibility(View.GONE);
                }

                // Cuando estamos en inicio mostramos las pistas y ocultamos los fragments
                if (seleccionMenu == R.id.nav_home) {
                    recyclerPistas.setVisibility(View.VISIBLE);
                    contenedorFragments.setVisibility(View.GONE);
                    BarTop.setVisibility(View.VISIBLE);
                    return true;
                }
                // Si pulsamos el item de perfil o ajustes ponemos visible su fragment y ocultamos las pistas
                if (seleccionMenu == R.id.nav_perfil) {
                    recyclerPistas.setVisibility(View.GONE);
                    contenedorFragments.setVisibility(View.VISIBLE);
                    BarTop.setVisibility(View.GONE);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenedorFragments, new PerfilFragment()).commit();
                    return true;
                }
                if (seleccionMenu == R.id.nav_ajustes) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar pista por nombre...");

        // Configurar colores del SearchView
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(ContextCompat.getColor(this, R.color.camposPerfil));
            searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.colorHint));
        }

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.camposPerfil));
        }

        ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeIcon != null) {
            closeIcon.setColorFilter(ContextCompat.getColor(this, R.color.camposPerfil));
        }

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                BarTop.post(() -> {
                    if (BarTop.getNavigationIcon() != null) {
                        BarTop.getNavigationIcon().setTint(ContextCompat.getColor(HomeActivity.this, R.color.camposPerfil));
                    }
                });
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                queryActual = "";
                aplicarFiltrosYOrden();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryActual = newText;
                aplicarFiltrosYOrden();
                return true;
            }
        });

        return true;
    }

    private void configurarVistaFiltros() {
        if (layoutFiltros == null) return;

        String[] opciones = {"Murcia", "Valencia", "Barcelona", "Sevilla", "Madrid", "Todas"};
        spinnerProvincia = layoutFiltros.findViewById(R.id.spinnerProvincia);
        rgOrden = layoutFiltros.findViewById(R.id.rgOrden);
        Button btnLimpiar = layoutFiltros.findViewById(R.id.btnLimpiar);
        Button btnCancelar = layoutFiltros.findViewById(R.id.btnCancelar);
        Button btnAplicar = layoutFiltros.findViewById(R.id.btnAplicar);

        // Configurar spinner con layout personalizado
        android.widget.ArrayAdapter<String> adapterSpinner = new android.widget.ArrayAdapter<>(this,
                R.layout.spinner_item_filtros, opciones);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item_filtros);
        spinnerProvincia.setAdapter(adapterSpinner);

        btnAplicar.setOnClickListener(v -> {
            provinciaSeleccionada = spinnerProvincia.getSelectedItem().toString();
            int checkedId = rgOrden.getCheckedRadioButtonId();
            if (checkedId == R.id.rbPrecioAsc) ordenSeleccionado = 1;
            else if (checkedId == R.id.rbPrecioDesc) ordenSeleccionado = 2;
            else ordenSeleccionado = 0;

            aplicarFiltrosYOrden();
            layoutFiltros.setVisibility(View.GONE);
        });

        btnCancelar.setOnClickListener(v -> layoutFiltros.setVisibility(View.GONE));

        btnLimpiar.setOnClickListener(v -> {
            provinciaSeleccionada = "Todas";
            ordenSeleccionado = 0;
            spinnerProvincia.setSelection(opciones.length - 1); // Seleccionar "Todas"
            rgOrden.check(R.id.rbSinOrden);
            aplicarFiltrosYOrden();
            layoutFiltros.setVisibility(View.GONE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (layoutFiltros.getVisibility() == View.VISIBLE) {
                layoutFiltros.setVisibility(View.GONE);
            } else {
                // Actualizar valores antes de mostrar
                for (int i = 0; i < spinnerProvincia.getCount(); i++) {
                    if (spinnerProvincia.getItemAtPosition(i).toString().equals(provinciaSeleccionada)) {
                        spinnerProvincia.setSelection(i);
                        break;
                    }
                }
                if (ordenSeleccionado == 1) rgOrden.check(R.id.rbPrecioAsc);
                else if (ordenSeleccionado == 2) rgOrden.check(R.id.rbPrecioDesc);
                else rgOrden.check(R.id.rbSinOrden);

                layoutFiltros.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void aplicarFiltrosYOrden() {
        if (listaOriginal == null) return;

        List<Pista> nuevaListaFiltrada = new java.util.ArrayList<>();

        for (Pista p : listaOriginal) {
            String nombre = p.getNombre() != null ? p.getNombre().toLowerCase() : "";
            String ubicacion = p.getUbicacion() != null ? p.getUbicacion() : "";

            boolean cumpleNombre = nombre.contains(queryActual.toLowerCase());
            
            // Si la ubicación contiene la provincia seleccionada (ej: "Murcia, Avenida...")
            boolean cumpleProvincia = provinciaSeleccionada.equals("Todas") ||
                    ubicacion.toLowerCase().contains(provinciaSeleccionada.toLowerCase());

            if (cumpleNombre && cumpleProvincia) {
                nuevaListaFiltrada.add(p);
            }
        }

        // Aplicar orden
        if (ordenSeleccionado == 1) {
            java.util.Collections.sort(nuevaListaFiltrada, (p1, p2) -> Double.compare(p1.getPrecioHora(), p2.getPrecioHora()));
        } else if (ordenSeleccionado == 2) {
            java.util.Collections.sort(nuevaListaFiltrada, (p1, p2) -> Double.compare(p2.getPrecioHora(), p1.getPrecioHora()));
        }

        listaFiltrada = nuevaListaFiltrada;
        if (adapter != null) {
            adapter.actualizarLista(listaFiltrada);
        }
    }

    private void preFetchDatosUsuario() {
        int userId = SessionManager.getInstance(this).getUsuarioId();
        if (userId != -1) {
            RetrofitCliente.getApiServicio().getUsuario(userId).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SessionManager.getInstance(HomeActivity.this).guardarSesion(response.body());
                        Log.d("PREFETCH", "Datos de usuario actualizados");
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    Log.e("PREFETCH", "Error al pre-cargar datos: " + t.getMessage());
                }
            });
        }
    }

    public void abrirReserva(Pista pista) {

        // Ocultar filtros si están abiertos al ir a reserva
        if (layoutFiltros != null) {
            layoutFiltros.setVisibility(View.GONE);
        }

        recyclerPistas.setVisibility(View.GONE);
        contenedorFragments.setVisibility(View.VISIBLE);
        contenedorFragments.bringToFront();
        BarTop.setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorFragments, ReservaFragment.newInstance(pista))
                .addToBackStack(null)
                .commit();
    }
}
