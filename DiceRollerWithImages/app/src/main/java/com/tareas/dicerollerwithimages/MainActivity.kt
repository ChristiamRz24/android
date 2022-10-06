package com.tareas.dicerollerwithimages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtener el botón por su id
        val btnGirar: Button = findViewById(R.id.btnGirar)
        // Añadir el evento onClick al botón y girar el dado cuando se de ejecute
        btnGirar.setOnClickListener { rollDice() }
        // Girar el dado al iniciar la aplicación
        rollDice()
    }
    private fun rollDice() {
        // Crear una instancia del dado con 6 caras
        val miPrimerDado = Dice(6)
        // Girar el dado y guardar el resultado
        val resultadoDelGiro = miPrimerDado.roll()
        // Cambiar la imagen del dado
        changeImage(resultadoDelGiro)
    }

    private fun changeImage (resultadoDelGiro: Int) {
        // Obtener contenedor de la imagen por su id
        val imagenDelDado: ImageView = findViewById(R.id.diceImage)
        // Guardar la imagen adecuada en una variable de acuerdo al resultado del giro
        val nuevaImagenDelDado = when (resultadoDelGiro) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        // Cambiar la imagen del contenedor del dado
        imagenDelDado.setImageResource(nuevaImagenDelDado)
    }
}

class Dice(private val numSides: Int) {
    fun roll(): Int {
        // Retornar un número random de entre 1 y el total de caras del dado
        return (1..numSides).random()
    }
}