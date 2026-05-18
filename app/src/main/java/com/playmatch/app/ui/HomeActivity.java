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

    private String queryActual = "";
    private String provinciaSeleccionada = "Todas";
    private int ordenSeleccionado = 0;

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

        recyclerPistas.setLayoutManager((new LinearLayoutManager(this)));
        setSupportActionBar(BarTop);

        RetrofitCliente.getApiServicio().getPistas().enqueue(new Callback<List<Pista>>() {
            @Override
            public void onResponse(@NonNull Call<List<Pista>> call, @NonNull Response<List<Pista>> response) {
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
            public void onFailure(@NonNull Call<List<Pista>> call, @NonNull Throwable t) {
                Log.e("PISTAS", "Error al cargar las pistas " + t.getMessage());
            }
        });

        String nombreUsuario = SessionManager.getInstance(this).getNombre();
        BarTop.setTitle("Bienvenido " + nombreUsuario);

        preFetchDatosUsuario();

        BarMenu.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int seleccionMenu = item.getItemId();

                if (layoutFiltros != null) {
                    layoutFiltros.setVisibility(View.GONE);
                }

                if (seleccionMenu == R.id.nav_home) {
                    recyclerPistas.setVisibility(View.VISIBLE);
                    contenedorFragments.setVisibility(View.GONE);
                    BarTop.setVisibility(View.VISIBLE);
                    return true;
                }
                if (seleccionMenu == R.id.nav_reserva) {
                    recyclerPistas.setVisibility(View.GONE);
                    contenedorFragments.setVisibility(View.VISIBLE);
                    BarTop.setVisibility(View.GONE);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenedorFragments, new MisReservasFragment()).commit();
                    return true;
                }
                if (seleccionMenu == R.id.nav_partidos) {
                    recyclerPistas.setVisibility(View.GONE);
                    contenedorFragments.setVisibility(View.VISIBLE);
                    BarTop.setVisibility(View.GONE);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenedorFragments, new PartidosFragment()).commit();
                    return true;
                }
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
        assert searchView != null;
        searchView.setQueryHint("Buscar pista por nombre...");

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
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

        android.widget.ArrayAdapter<String> adapterSpinner = new android.widget.ArrayAdapter<>(this,
                R.layout.spinner_item_filtros, opciones);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item_filtros);
        spinnerProvincia.setAdapter(adapterSpinner);

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provinciaSeleccionada = spinnerProvincia.getSelectedItem().toString();
                int checkedId = rgOrden.getCheckedRadioButtonId();
                if (checkedId == R.id.rbPrecioAsc) ordenSeleccionado = 1;
                else if (checkedId == R.id.rbPrecioDesc) ordenSeleccionado = 2;
                else ordenSeleccionado = 0;

                aplicarFiltrosYOrden();
                layoutFiltros.setVisibility(View.GONE);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutFiltros.setVisibility(View.GONE);
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provinciaSeleccionada = "Todas";
                ordenSeleccionado = 0;
                spinnerProvincia.setSelection(opciones.length - 1);
                rgOrden.check(R.id.rbSinOrden);
                aplicarFiltrosYOrden();
                layoutFiltros.setVisibility(View.GONE);
            }
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
            boolean cumpleProvincia = provinciaSeleccionada.equals("Todas") ||
                    ubicacion.toLowerCase().contains(provinciaSeleccionada.toLowerCase());

            if (cumpleNombre && cumpleProvincia) {
                nuevaListaFiltrada.add(p);
            }
        }

        // Aplicar orden
        if (ordenSeleccionado == 1) {
            nuevaListaFiltrada.sort(new java.util.Comparator<Pista>() {
                @Override
                public int compare(Pista p1, Pista p2) {
                    return Double.compare(p1.getPrecioHora(), p2.getPrecioHora());
                }
            });
        } else if (ordenSeleccionado == 2) {
            nuevaListaFiltrada.sort(new java.util.Comparator<Pista>() {
                @Override
                public int compare(Pista p1, Pista p2) {
                    return Double.compare(p2.getPrecioHora(), p1.getPrecioHora());
                }
            });
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
                public void onResponse(@NonNull Call<Usuario> call, @NonNull Response<Usuario> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SessionManager.getInstance(HomeActivity.this).guardarSesion(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Usuario> call, @NonNull Throwable t) {
                    Log.e("PISTAS", "Error al pre-cargar datos");
                }
            });
        }
    }

    public void abrirReserva(Pista pista) {
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
