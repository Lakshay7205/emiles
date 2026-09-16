package com.state.greenmiles.com_state_greenmiles.presentation.screens.splash_screen

import androidx.lifecycle.ViewModel
import com.state.greenmiles.com_state_greenmiles.utils.LocalStorage

class SplashViewmodel(
    private val localStorage: LocalStorage
) : ViewModel() {

    fun checkIfTokenPresent(): Boolean {
        return !localStorage.getToken().isNullOrBlank()
    }


}