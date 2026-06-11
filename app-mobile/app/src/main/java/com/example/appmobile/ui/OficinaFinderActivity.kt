package com.example.appmobile.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.appmobile.R

class OficinaFinderActivity : AppCompatActivity() {

    private lateinit var imagemMapa: ImageView
    private lateinit var buttonAbrirMaps: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oficina_finder)

        imagemMapa = findViewById(R.id.imagemMapa)
        buttonAbrirMaps = findViewById(R.id.buttonAbrirMaps)

        buttonAbrirMaps.setOnClickListener { abrirGoogleMaps() }
    }

    private fun abrirGoogleMaps() {
        val gmmIntentUri = Uri.parse("geo:0,0?q=oficinas mecânicas próximas")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/oficinas+mecanicas"))
            startActivity(webIntent)
        }
    }
}
