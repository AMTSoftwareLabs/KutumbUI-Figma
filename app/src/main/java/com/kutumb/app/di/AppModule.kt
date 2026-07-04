package com.kutumb.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.kutumb.app.data.dao.AppDao
import com.kutumb.app.data.database.KutumbDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kutumb_settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): KutumbDatabase =
        Room.databaseBuilder(ctx, KutumbDatabase::class.java, "kutumb.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton
    fun provideDao(db: KutumbDatabase): AppDao = db.dao()

    @Provides @Singleton
    fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
        ctx.dataStore
}
