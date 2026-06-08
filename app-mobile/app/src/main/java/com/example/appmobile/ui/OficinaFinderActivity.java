package com.example.appmobile.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appmobile.R;

public class OficinaFinderActivity extends AppCompatActivity {
    
    private ImageView imagemMapa;
    private Button buttonAbrirMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oficina_finder);
        
        imagemMapa = findViewById(R.id.imagemMapa);
        buttonAbrirMaps = findViewById(R.id.buttonAbrirMaps);
        
        // Você pode carregar uma imagem estática aqui
        // imagemMapa.setImageResource(R.drawable.mapa_icon);
        
        buttonAbrirMaps.setOnClickListener(v -> abrirGoogleMaps());
    }

    private void abrirGoogleMaps() {
        // Intent implícita para abrir Google Maps
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=oficinas mecânicas próximas");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback - abrir no navegador
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/oficinas+mecanicas"));
            startActivity(webIntent);
        }
    }
}
