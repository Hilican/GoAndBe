package com.github.hilican.goandbe.di

import android.content.Context
import androidx.room.Room
import com.github.hilican.goandbe.BuildConfig
import com.github.hilican.goandbe.data.Room.AppDatabase
import com.github.hilican.goandbe.data.Room.TripDao
import com.github.hilican.goandbe.data.Room.UserDao
import com.github.hilican.goandbe.data.remote.api.IHotelApiService
import com.github.hilican.goandbe.repo.implementations.AuthRepository
import com.github.hilican.goandbe.repo.implementations.HotelApiRepository
import com.github.hilican.goandbe.repo.implementations.TripRepository
import com.github.hilican.goandbe.repo.interfaces.IAuthRepository
import com.github.hilican.goandbe.repo.interfaces.IHotelApiRepository
import com.github.hilican.goandbe.repo.interfaces.ITripRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideHotelApi(): IHotelApiService = Retrofit.Builder()
            .baseUrl(BuildConfig.HOTELS_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IHotelApiService::class.java)

    @Provides
    @Singleton
    fun provideHotelRepository(
        apiService: IHotelApiService
    ): IHotelApiRepository = HotelApiRepository(apiService)

    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao
    ): ITripRepository = TripRepository(tripDao)

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        firebaseAuth: FirebaseAuth
    ): IAuthRepository = AuthRepository(userDao, firebaseAuth)
}