package com.codingstuff.loginandsignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import android.widget.EditText
import com.codingstuff.loginandsignup.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnCerrar.setOnClickListener {
            firebaseAuth.signOut() // Cerrar sesión en Firebase
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Limpiar la pila de actividades
            startActivity(intent) // Iniciar la actividad de inicio de sesión
            finish() // Finalizar la actividad actual
        }

        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener{
            val lanzar = Intent(this, AltaMascota::class.java)
            startActivity(lanzar)
        }
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener{
            val maps = Intent(this, activity_maps::class.java)
            startActivity(maps)
        }

    }





}