package com.example.resepapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resep_table")
data class Resep(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val deskripsiSingkat: String,
    val deskripsiLengkap: String,
    val bahan: List<String>, // Akan disimpan sebagai String dan di-convert
    val langkahLangkah: List<String>, // Akan disimpan sebagai String dan di-convert
    val gambarPath: String? // Path ke gambar lokal atau URL jika nanti diperlukan
)