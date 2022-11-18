package com.google.firebase.example.osblogapp.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Operating System POJO.
 */
@IgnoreExtraProperties
data class OperatingSystem(
    var name: String? = null,
    var category: String? = null,
    var photo: String? = null,
    var price: Int = 0,
    var numRatings: Int = 0,
    var avgRating: Double = 0.toDouble()
) {

    companion object {
        const val FIELD_CATEGORY = "category"
        const val FIELD_PRICE = "price"
        const val FIELD_POPULARITY = "numRatings"
        const val FIELD_AVG_RATING = "avgRating"
    }
}
