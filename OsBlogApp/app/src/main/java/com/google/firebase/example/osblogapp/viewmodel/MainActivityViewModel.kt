package com.google.firebase.example.osblogapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.example.osblogapp.Filters

/**
 * ViewModel for [com.google.firebase.example.osblogapp.MainActivity].
 */

class MainActivityViewModel : ViewModel() {

    var isSigningIn: Boolean = false
    var filters: Filters = Filters.default
}
