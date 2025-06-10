package com.example.resepapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resep: Resep)

    @Update
    suspend fun update(resep: Resep)

    @Delete
    suspend fun delete(resep: Resep)

    @Query("SELECT * FROM resep_table ORDER BY nama ASC")
    fun getAllResep(): Flow<List<Resep>>

    @Query("SELECT * FROM resep_table WHERE id = :id")
    fun getResepById(id: Int): Flow<Resep>

    // Query untuk fitur pencarian
    @Query("SELECT * FROM resep_table WHERE nama LIKE '%' || :query || '%' OR deskripsiSingkat LIKE '%' || :query || '%' ORDER BY nama ASC")
    fun searchResep(query: String): Flow<List<Resep>>
}