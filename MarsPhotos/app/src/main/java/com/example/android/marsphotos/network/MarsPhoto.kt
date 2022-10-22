package com.example.android.marsphotos.network

import com.squareup.moshi.Json

/**
 * Esta clase de datos define una foto de Marte que incluye un ID y la URL de la imagen.
 * Moshi utiliza los nombres de propiedad de esta clase de datos para hacer coincidir los
 * nombres de los valores en JSON.
 */
data class MarsPhoto(
    val id: String,
    @Json(name = "img_src") val imgSrcUrl: String
)
