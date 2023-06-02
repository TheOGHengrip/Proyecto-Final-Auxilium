package com.codingstuff.loginandsignup
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.codingstuff.loginandsignup.databinding.ActivityAltaMascotaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import net.glxn.qrgen.android.QRCode

class AltaMascota : AppCompatActivity() {


    private lateinit var binding: ActivityAltaMascotaBinding
    private lateinit var etMasNom: EditText
    private lateinit var etMasSexo: EditText
    private lateinit var etMasEdad: EditText
    private lateinit var etMasRaza: EditText
    private lateinit var btnQR: Button
    private lateinit var dbRef: DatabaseReference
    private var storageRef = Firebase.storage
    private lateinit var boton2qr: Button

    private lateinit var uri: Uri

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alta_mascota)
        binding = ActivityAltaMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etMasNom = findViewById(R.id.NombreAlta)
        etMasSexo = findViewById(R.id.Sexo)
        etMasEdad = findViewById(R.id.Edad)
        etMasRaza = findViewById(R.id.Raza)
        btnQR = findViewById(R.id.GenerarQR)
        boton2qr = findViewById(R.id.button3)

        dbRef = FirebaseDatabase.getInstance().getReference("Mascotas")
        storageRef = FirebaseStorage.getInstance()

        //Tomar foto de la cámara
        val fotomascotaBtn = findViewById<Button>(R.id.fotomascotaBtn)
        fotomascotaBtn.setOnClickListener {
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
        val getImage  = registerForActivityResult(
            ActivityResultContracts.GetContent(), ActivityResultCallback {

                binding.fotomascotaBtn.setImageURI(it)
                uri = it
            }
        )

        binding.fotomascotaBtn.setOnClickListener{
            getImage.launch("image/*")
        }

        btnQR.setOnClickListener{
            saveMascotasData()
            storageRef.getReference("Images").child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid

                            val mapImage = mapOf(
                                "url" to it.toString()
                            )
                            val databaseReference = FirebaseDatabase.getInstance().getReference("userimages")
                            databaseReference.child(userId).setValue(mapImage)

                        }
                }
        }
        boton2qr.setOnClickListener{
            val lanzar = Intent(this, AltaMascota::class.java)
            startActivity(lanzar)
        }

        val buttonImg = findViewById<ImageButton>(R.id.Menu)
        buttonImg.setOnClickListener{
            val lanzar = Intent(this, MainActivity::class.java)
            startActivity(lanzar)
        }
    }


    private fun saveMascotasData() {
        // tomar valores
        val mascotaNombre = etMasNom.text.toString()
        val mascotaSexo = etMasSexo.text.toString()
        val mascotaEdad = etMasEdad.text.toString()
        val mascotaRaza = etMasRaza.text.toString()

        if (mascotaNombre.isEmpty()) {
            etMasNom.error = "Nombre de la mascota requerido"
        }
        if (mascotaSexo.isEmpty()) {
            etMasNom.error = "Sexo de la mascota requerido"
        }
        if (mascotaEdad.isEmpty()) {
            etMasNom.error = "Edad de la mascota requerida"
        }
        if (mascotaRaza.isEmpty()) {
            etMasNom.error = "Raza de la mascota requerida"
        }

        val mascotaId = dbRef.push().key!!
        var mascota: MascotaModelo

        mascota = MascotaModelo(mascotaId, mascotaNombre, mascotaSexo, mascotaEdad, mascotaRaza)

        dbRef.child(mascotaId).setValue(mascota)
            .addOnCompleteListener {
                Toast.makeText(this, "Se genero el codigo QR correctamente", Toast.LENGTH_LONG)
                    .show()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }


        val bitmap = QRCode.from("Nombre: $mascotaNombre, Sexo: $mascotaSexo, Edad:$mascotaEdad ,Raza:$mascotaRaza ").withSize(500, 500).bitmap()
        binding.fotomascotaBtn.setImageBitmap(bitmap)

    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val intent = result.data
            val imageBitmap= intent?.extras?.get("data") as Bitmap
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(imageBitmap)
        }
    }
}