package com.google.firebase.example.osblogapp

import android.content.Context
import android.text.TextUtils
import com.google.firebase.example.osblogapp.model.OperatingSystem
import com.google.firebase.example.osblogapp.util.OsPostUtil
import com.google.firebase.firestore.Query

/**
 * Object for passing filters around.
 */
class Filters {

    var category: String? = null
    var price = -1
    var sortBy: String? = null
    var sortDirection: Query.Direction = Query.Direction.DESCENDING

    fun hasCategory(): Boolean {
        return !TextUtils.isEmpty(category)
    }

    fun hasPrice(): Boolean {
        return price > 0
    }

    fun hasSortBy(): Boolean {
        return !TextUtils.isEmpty(sortBy)
    }

    fun getSearchDescription(context: Context): String {
        val desc = StringBuilder()

        if (category == null) {
            desc.append("<b>")
            desc.append(context.getString(R.string.all_restaurants))
            desc.append("</b>")
        }

        if (category != null) {
            desc.append("<b>")
            desc.append(category)
            desc.append("</b>")
        }

        if (price > 0) {
            desc.append(" for ")
            desc.append("<b>")
            desc.append(OsPostUtil.getPriceString(price))
            desc.append("</b>")
        }

        return desc.toString()
    }

    fun getOrderDescription(context: Context): String {
        return when (sortBy) {
            OperatingSystem.FIELD_PRICE -> context.getString(R.string.sorted_by_price)
            OperatingSystem.FIELD_POPULARITY -> context.getString(R.string.sorted_by_popularity)
            else -> context.getString(R.string.sorted_by_rating)
        }
    }

    companion object {

        val default: Filters
            get() {
                val filters = Filters()
                filters.sortBy = OperatingSystem.FIELD_AVG_RATING
                filters.sortDirection = Query.Direction.DESCENDING

                return filters
            }
    }
}
