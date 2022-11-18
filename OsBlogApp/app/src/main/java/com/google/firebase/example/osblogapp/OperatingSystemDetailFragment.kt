package com.google.firebase.example.osblogapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.example.osblogapp.adapter.RatingAdapter
import com.google.firebase.example.osblogapp.databinding.FragmentOperatingSystemDetailBinding
import com.google.firebase.example.osblogapp.model.OperatingSystem
import com.google.firebase.example.osblogapp.model.Rating
import com.google.firebase.example.osblogapp.util.OsPostUtil
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class OperatingSystemDetailFragment : Fragment(),
    EventListener<DocumentSnapshot>,
    RatingDialogFragment.RatingListener {

    private var ratingDialog: RatingDialogFragment? = null

    private lateinit var binding: FragmentOperatingSystemDetailBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var operatingSystemRef: DocumentReference
    private lateinit var ratingAdapter: RatingAdapter

    private var operatingSystemRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOperatingSystemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get restaurant ID from extras
        val operatingSystemId =
            OperatingSystemDetailFragmentArgs.fromBundle(requireArguments()).keyRestaurantId

        // Initialize Firestore
        firestore = Firebase.firestore

        // Get reference to the restaurant
        operatingSystemRef = firestore.collection("osposts").document(operatingSystemId)

        // Get ratings
        val ratingsQuery = operatingSystemRef
            .collection("ratings")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)

        // RecyclerView
        ratingAdapter = object : RatingAdapter(ratingsQuery) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    binding.recyclerRatings.visibility = View.GONE
                    binding.viewEmptyRatings.visibility = View.VISIBLE
                } else {
                    binding.recyclerRatings.visibility = View.VISIBLE
                    binding.viewEmptyRatings.visibility = View.GONE
                }
            }
        }
        binding.recyclerRatings.layoutManager = LinearLayoutManager(context)
        binding.recyclerRatings.adapter = ratingAdapter

        ratingDialog = RatingDialogFragment()

        binding.operatingSystemBack.setOnClickListener { onBackArrowClicked() }
        binding.fabShowRatingDialog.setOnClickListener { onAddRatingClicked() }
    }

    override fun onStart() {
        super.onStart()

        ratingAdapter.startListening()
        operatingSystemRegistration = operatingSystemRef.addSnapshotListener(this)
    }

    override fun onStop() {
        super.onStop()

        ratingAdapter.stopListening()

        operatingSystemRegistration?.remove()
        operatingSystemRegistration = null
    }

    /**
     * Listener for the Restaurant document ([.restaurantRef]).
     */
    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, "Operating System: onEvent", e)
            return
        }

        snapshot?.let {
            val operatingSystem = snapshot.toObject<OperatingSystem>()

            if (operatingSystem != null) {
                onOperatingSystemLoaded(operatingSystem)
            }
        }
    }

    private fun onOperatingSystemLoaded(os: OperatingSystem) {
        binding.operatingSystemName.text = os.name
        binding.operatingSystemRating.rating = os.avgRating.toFloat()
        binding.operatingSystemNumRatings.text = getString(R.string.fmt_num_ratings, os.numRatings)
        binding.operatingSystemCategory.text = os.category
        binding.operatingSystemPrice.text = OsPostUtil.getPriceString(os)

        // Background image
        Glide.with(binding.operatingSystemImage.context)
            .load(os.photo)
            .into(binding.operatingSystemImage)
    }

    private fun onBackArrowClicked() {
        requireActivity().onBackPressed()
    }

    private fun onAddRatingClicked() {
        ratingDialog?.show(childFragmentManager, RatingDialogFragment.TAG)
    }

    override fun onRating(rating: Rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(operatingSystemRef, rating)
            .addOnSuccessListener(requireActivity()) {
                Log.d(TAG, "Rating added")

                // Hide keyboard and scroll to top
                hideKeyboard()
                binding.recyclerRatings.smoothScrollToPosition(0)
            }
            .addOnFailureListener(requireActivity()) { e ->
                Log.w(TAG, "Add rating failed", e)

                // Show failure message and hide keyboard
                hideKeyboard()
                Snackbar.make(
                    requireView().findViewById(android.R.id.content), "Failed to add rating",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }

    private fun addRating(OperatingSystemRef: DocumentReference, rating: Rating): Task<Void> {
        val ratingRef = OperatingSystemRef.collection("ratings").document()

        return firestore.runTransaction { transaction ->
            val osTransaction = transaction.get(OperatingSystemRef).toObject<OperatingSystem>()
                ?: throw Exception("Operating System not found at ${OperatingSystemRef.path}")

            // Compute new number of ratings
            val newNumRatings = osTransaction.numRatings + 1

            // Compute new average rating
            val oldRatingTotal = osTransaction.avgRating * osTransaction.numRatings
            val newAvgRating = (oldRatingTotal + rating.rating) / newNumRatings

            // Set new operating system info
            osTransaction.numRatings = newNumRatings
            osTransaction.avgRating = newAvgRating

            // Commit to Firestore
            transaction.set(OperatingSystemRef, osTransaction)
            transaction.set(ratingRef, rating)

            null
        }
    }

    private fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    companion object {

        private const val TAG = "RestaurantDetail"

        const val KEY_RESTAURANT_ID = "key_restaurant_id"
    }
}
