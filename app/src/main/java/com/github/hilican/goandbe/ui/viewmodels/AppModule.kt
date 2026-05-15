package com.github.hilican.goandbe.ui.viewmodels

import android.content.Context
import androidx.room.Room
import com.github.hilican.goandbe.data.AppDatabase
import com.github.hilican.goandbe.data.repositories.AuthRepository
import com.github.hilican.goandbe.domain.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "go_and_be_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Provides
    fun provideTripDao(db: AppDatabase) = db.tripDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton // Opcional: para que sea una única instancia en toda la app
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepository // Le pasas la clase REAL
    ): IAuthRepository // Y Hilt devolverá la INTERFAZ cuando se la pidan
}