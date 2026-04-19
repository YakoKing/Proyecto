package com.playmatch.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.playmatch.app.R;


public class HomeActivity extends AppCompatActivity {

    private ImageView imgFondo;
    private MaterialToolbar BarTop;
    private BottomNavigationView BarMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Enlazar variables del xml con java
        imgFondo=findViewById(R.id.imgFondo);
        BarMenu=findViewById(R.id.BarMenu);
        BarTop=findViewById(R.id.BarTop);

        //Recogemos el texto del textView de la plantalla del login
        String nombreUsuario=getIntent().getStringExtra("nombre_usuario");
        BarTop.setTitle("Bienvenido " + nombreUsuario);
    }
}
