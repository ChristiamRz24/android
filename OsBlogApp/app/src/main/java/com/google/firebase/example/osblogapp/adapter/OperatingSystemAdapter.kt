package com.google.firebase.example.osblogapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.example.osblogapp.R
import com.google.firebase.example.osblogapp.databinding.ItemOperatingSystemBinding
import com.google.firebase.example.osblogapp.model.OperatingSystem
import com.google.firebase.example.osblogapp.util.OsPostUtil
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

/**
 * RecyclerView adapter for a list of Restaurants.
 */
open class OperatingSystemAdapter(
    query: Query,
    private val listener: OnOperatingSystemSelectedListener
) :
    FirestoreAdapter<OperatingSystemAdapter.ViewHolder>(query) {

    interface OnOperatingSystemSelectedListener {

        fun onOperatingSystemSelected(operatingSystem: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOperatingSystemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(val binding: ItemOperatingSystemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnOperatingSystemSelectedListener?
        ) {

            val operatingSystem = snapshot.toObject<OperatingSystem>() ?: return

            val resources = binding.root.resources

            // Load image
            Glide.with(binding.osItemImage.context)
                .load(operatingSystem.photo)
                .into(binding.osItemImage)

            val numRatings: Int = operatingSystem.numRatings

            binding.osItemName.text = operatingSystem.name
            binding.osItemRating.rating = operatingSystem.avgRating.toFloat()
            // binding.restaurantItemCity.text = restaurant.city
            binding.osItemCategory.text = operatingSystem.category
            binding.osItemNumRatings.text = resources.getString(
                R.string.fmt_num_ratings,
                numRatings
            )
            binding.osItemPrice.text = OsPostUtil.getPriceString(operatingSystem)

            // Click listener
            binding.root.setOnClickListener {
                listener?.onOperatingSystemSelected(snapshot)
            }
        }
    }
}
