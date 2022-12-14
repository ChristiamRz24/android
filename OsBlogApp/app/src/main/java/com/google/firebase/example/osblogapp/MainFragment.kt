package com.google.firebase.example.osblogapp

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.example.osblogapp.adapter.OperatingSystemAdapter
import com.google.firebase.example.osblogapp.databinding.FragmentMainBinding
import com.google.firebase.example.osblogapp.model.OperatingSystem
import com.google.firebase.example.osblogapp.util.OsPostUtil
import com.google.firebase.example.osblogapp.viewmodel.MainActivityViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment(),
    FilterDialogFragment.FilterListener,
    OperatingSystemAdapter.OnOperatingSystemSelectedListener {

    lateinit var firestore: FirebaseFirestore
    private var query: Query? = null

    private lateinit var binding: FragmentMainBinding
    private lateinit var filterDialog: FilterDialogFragment
    private var adapter: OperatingSystemAdapter? = null

    private lateinit var viewModel: MainActivityViewModel
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result -> this.onSignInResult(result) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View model
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)

        // Firestore
        firestore = Firebase.firestore

        // Get data from FireStore
        query = firestore.collection("osposts")
            .orderBy("avgRating", Query.Direction.DESCENDING)
            .limit(LIMIT.toLong())

        // RecyclerView
        query?.let {
            adapter = object : OperatingSystemAdapter(it, this@MainFragment) {
                override fun onDataChanged() {
                    // Show/hide content if the query returns empty.
                    if (itemCount == 0) {
                        binding.recyclerOperatingSystems.visibility = View.GONE
                        binding.viewEmpty.visibility = View.VISIBLE
                    } else {
                        binding.recyclerOperatingSystems.visibility = View.VISIBLE
                        binding.viewEmpty.visibility = View.GONE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    // Show a snackbar on errors
                    Snackbar.make(
                        binding.root,
                        "Error: check logs for info.", Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            binding.recyclerOperatingSystems.adapter = adapter
        }

        binding.recyclerOperatingSystems.layoutManager = LinearLayoutManager(context)

        // Filter Dialog
        filterDialog = FilterDialogFragment()

        binding.filterBar.setOnClickListener { onFilterClicked() }
        binding.buttonClearFilter.setOnClickListener { onClearFilterClicked() }
    }

    override fun onStart() {
        super.onStart()

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn()
            return
        }

        // Apply filters
        onFilter(viewModel.filters)

        // Start listening for Firestore updates
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_items -> onAddItemsClicked()
            R.id.menu_sign_out -> {
                AuthUI.getInstance().signOut(requireContext())
                startSignIn()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        viewModel.isSigningIn = false

        if (result.resultCode != Activity.RESULT_OK) {
            if (response == null) {
                // User pressed the back button.
                requireActivity().finish()
            } else if (response.error != null && response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                showSignInErrorDialog(R.string.message_no_network)
            } else {
                showSignInErrorDialog(R.string.message_unknown)
            }
        }
    }

    private fun onFilterClicked() {
        // Show the dialog containing filter options
        filterDialog.show(childFragmentManager, FilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        filterDialog.resetFilters()

        onFilter(Filters.default)
    }

    override fun onOperatingSystemSelected(operatingSystem: DocumentSnapshot) {
        // Go to the details page for the selected restaurant

        val action = MainFragmentDirections
            .actionMainFragmentToRestaurantDetailFragment(operatingSystem.id)

        // Toast.makeText(context, "Id: $operatingSystem.id", Toast.LENGTH_LONG).show()

        findNavController().navigate(action)
    }

    override fun onFilter(filters: Filters) {
        var query: Query = firestore.collection("osposts")

        if (filters.hasCategory()) {
            query = query.whereEqualTo(OperatingSystem.FIELD_CATEGORY, filters.category)
        }

        if (filters.hasPrice()) {
            query = query.whereEqualTo(OperatingSystem.FIELD_PRICE, filters.price)
        }

        if (filters.hasSortBy()) {
            query = query.orderBy(filters.sortBy.toString(), filters.sortDirection)
        }

        query = query.limit(LIMIT.toLong())

        adapter?.setQuery(query)

        // Set header
        binding.textCurrentSearch.text = HtmlCompat.fromHtml(
            filters.getSearchDescription(requireContext()),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.textCurrentSortBy.text = filters.getOrderDescription(requireContext())

        // Save filters
        viewModel.filters = filters
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSigningIn && Firebase.auth.currentUser == null
    }

    private fun startSignIn() {
        // Sign in with FirebaseUI
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
            .setIsSmartLockEnabled(false)
            .build()

        signInLauncher.launch(intent)
        viewModel.isSigningIn = true
    }

    private fun onAddItemsClicked() {
        // showTodoToast()
        val osPostsRef = firestore.collection("osposts")
        for (i in 0..9) {
            // Crear random os post / calificaci??n
            val randomOsPost = OsPostUtil.getRandom(requireContext())
            // A??adir los restaurantes
            osPostsRef.add(randomOsPost)
        }
    }

    private fun showSignInErrorDialog(@StringRes message: Int) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.title_sign_in_error)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.option_retry) { _, _ -> startSignIn() }
            .setNegativeButton(R.string.option_exit) { _, _ -> requireActivity().finish() }.create()

        dialog.show()
    }

    companion object {

        private const val TAG = "MainActivity"

        private const val LIMIT = 50
    }
}
