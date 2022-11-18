package com.google.firebase.example.osblogapp.util

import android.content.Context
import com.google.firebase.example.osblogapp.R
import com.google.firebase.example.osblogapp.model.OperatingSystem
import java.util.*

/**
 * Utilities for Operating Systems.
 */
object OsPostUtil {

    private const val OS_BASE_URL =
        "https://res.cloudinary.com/doyohukci/image/upload/osblogapp/%d"
    private const val MAX_IMAGE_NUM = 10

    private val OS_NAME = arrayOf(
        "Windows 10", "Mac OS", "Unix", "Solaris", "FreeBSD",
        "OpenBSD", "Android", "Chrome OS", "IOS", "Debian"
    )

    /**
     * Create a random Restaurant POJO.
     */
    fun getRandom(context: Context): OperatingSystem {
        val operatingSystem = OperatingSystem()
        val random = Random()
        var categories = context.resources.getStringArray(R.array.categories)
        categories = categories.copyOfRange(1, categories.size)

        val prices = intArrayOf(1, 2, 3)

        operatingSystem.name = getOsRandomName(random)
        operatingSystem.category = getRandomString(categories, random)
        operatingSystem.photo = getRandomImageUrl(random)
        operatingSystem.price = getRandomInt(prices, random)
        operatingSystem.numRatings = random.nextInt(20)

        return operatingSystem
    }

    /**
     * Get a random image.
     */
    private fun getRandomImageUrl(random: Random): String {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        val id = random.nextInt(MAX_IMAGE_NUM) + 1

        return String.format(Locale.getDefault(), OS_BASE_URL, id)
    }

    /**
     * Get price represented as dollar signs.
     */
    fun getPriceString(restaurant: OperatingSystem): String {
        return getPriceString(restaurant.price)
    }

    /**
     * Get price represented as dollar signs.
     */
    fun getPriceString(priceInt: Int): String {
        return when (priceInt) {
            1 -> "License required"
            2 -> "Free"
            3 -> "Unknown"
            else -> "Unknown"
        }
    }

    // Ok
    private fun getOsRandomName(random: Random): String {
        return (getRandomString(OS_NAME, random))
    }

    private fun getRandomString(array: Array<String>, random: Random): String {
        val ind = random.nextInt(array.size)
        return array[ind]
    }

    private fun getRandomInt(array: IntArray, random: Random): Int {
        val ind = random.nextInt(array.size)
        return array[ind]
    }
}
