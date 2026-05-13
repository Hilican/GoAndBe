package com.github.hilican.goandbe.ui.viewmodels

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.hilican.goandbe.data.repositories.AuthRepository
import com.github.hilican.goandbe.data.repositories.TripRepository

object AppViewModelProvider {
    fun factory(repository1: AuthRepository, repository2: TripRepository) = viewModelFactory {
        initializer {
            AuthViewModel(repository = repository1)
        }

        initializer {
            TripListViewModel(repository = repository2)
        }
    }
}