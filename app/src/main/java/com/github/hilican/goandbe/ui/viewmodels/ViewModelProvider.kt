package com.github.hilican.goandbe.ui.viewmodels

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.hilican.goandbe.data.UserDao
import com.github.hilican.goandbe.domain.TripRepository

object AppViewModelProvider {
    fun factory(userDao: UserDao, repository: TripRepository) = viewModelFactory {
        // Aquí defines cómo se construye el AuthViewModel
        initializer {
            AuthViewModel(userDao = userDao)
        }

        initializer {
            TripListViewModel(repository = repository)
        }
    }
}