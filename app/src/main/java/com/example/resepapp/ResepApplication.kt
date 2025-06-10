package com.example.resepapp

import android.app.Application
import com.example.resepapp.data.ResepDatabase
import com.example.resepapp.data.ResepRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ResepApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { ResepDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ResepRepository(database.resepDao()) }
}