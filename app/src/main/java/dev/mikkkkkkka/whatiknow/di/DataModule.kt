package dev.mikkkkkkka.whatiknow.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mikkkkkkka.whatiknow.BuildConfig
import dev.mikkkkkkka.whatiknow.data.local.NoteMarkDatabase
import dev.mikkkkkkka.whatiknow.data.local.dao.MarkDao
import dev.mikkkkkkka.whatiknow.data.local.dao.NoteDao
import dev.mikkkkkkka.whatiknow.data.local.dao.PendingDeletionDao
import dev.mikkkkkkka.whatiknow.data.mapper.RoomMarkEntityMapper
import dev.mikkkkkkka.whatiknow.data.mapper.RoomNoteEntityMapper
import dev.mikkkkkkka.whatiknow.data.remote.ApiErrorParser
import dev.mikkkkkkka.whatiknow.data.remote.api.AuthApi
import dev.mikkkkkkka.whatiknow.data.remote.api.EchoApi
import dev.mikkkkkkka.whatiknow.data.remote.api.MarksApi
import dev.mikkkkkkka.whatiknow.data.remote.api.NotesApi
import dev.mikkkkkkka.whatiknow.data.session.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideNoteMarkDatabase(
        @ApplicationContext context: Context,
    ): NoteMarkDatabase {
        return Room.databaseBuilder(
            context,
            NoteMarkDatabase::class.java,
            "note.db"
        ).addMigrations(NoteMarkDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideNoteDao(
        database: NoteMarkDatabase,
    ): NoteDao = database.noteDao()

    @Provides
    fun provideMarkDao(
        database: NoteMarkDatabase,
    ): MarkDao = database.markDao()

    @Provides
    fun providePendingDeletionDao(
        database: NoteMarkDatabase,
    ): PendingDeletionDao = database.pendingDeletionDao()

    @Provides
    fun provideRoomNoteEntityMapper(): RoomNoteEntityMapper = RoomNoteEntityMapper()

    @Provides
    fun provideRoomMarkEntityMapper(): RoomMarkEntityMapper = RoomMarkEntityMapper()

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences {
        return context.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit,
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideNotesApi(
        retrofit: Retrofit,
    ): NotesApi = retrofit.create(NotesApi::class.java)

    @Provides
    @Singleton
    fun provideMarksApi(
        retrofit: Retrofit,
    ): MarksApi = retrofit.create(MarksApi::class.java)

    @Provides
    @Singleton
    fun provideEchoApi(
        retrofit: Retrofit,
    ): EchoApi = retrofit.create(EchoApi::class.java)

    @Provides
    @Singleton
    fun provideApiErrorParser(
        gson: Gson,
    ): ApiErrorParser = ApiErrorParser(gson)
}
