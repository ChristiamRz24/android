package com.tareas.diceroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtener el botón por su id
        val btnGirar: Button = findViewById(R.id.btnGirar)
        // Añadir el evento onClick al botón y girar el dado cuando se de ejecute
        btnGirar.setOnClickListener { rollDice() }
    }
    private fun rollDice() {
        // Crear una instancia del dado con 6 caras
        val miPrimerDado = Dice(6)
        // Girar el dado y guardar el resultado
        val resultadoDelGiro = miPrimerDado.roll()
        // Obtener el text con el resultado en pantalla por su id
        val resultadoEnPantalla: TextView = findViewById(R.id.textView)
        // Cambiar el texto mostrado por el resultado del giro del dado
        resultadoEnPantalla.text = resultadoDelGiro.toString()
    }
}

class Dice(private val numSides: Int) {
    fun roll(): Int {
        // Retornar un número random de entre 1 y el total de caras del dado
        return (1..numSides).random()
    }
}
