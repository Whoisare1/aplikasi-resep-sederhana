package com.example.resepapp.data

import kotlinx.coroutines.flow.Flow

class ResepRepository(private val resepDao: ResepDao) {

    val allResep: Flow<List<Resep>> = resepDao.getAllResep()

    suspend fun insert(resep: Resep) {
        resepDao.insert(resep)
    }

    suspend fun update(resep: Resep) {
        resepDao.update(resep)
    }

    suspend fun delete(resep: Resep) {
        resepDao.delete(resep)
    }

    fun getResepById(id: Int): Flow<Resep> {
        return resepDao.getResepById(id)
    }

    fun searchResep(query: String): Flow<List<Resep>> {
        return resepDao.searchResep(query)
    }
}