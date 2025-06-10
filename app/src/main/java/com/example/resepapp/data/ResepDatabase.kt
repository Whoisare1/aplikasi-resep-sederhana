package com.example.resepapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Resep::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Anotasi ini tetap diperlukan untuk Room menemukan TypeConverter Anda
abstract class ResepDatabase : RoomDatabase() {

    abstract fun resepDao(): ResepDao

    companion object {
        @Volatile
        private var INSTANCE: ResepDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ResepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ResepDatabase::class.java,
                    "resep_database" // Nama file database
                )
                    // Hapus baris .addTypeConverter(Converters::class) jika ada di sini, karena sudah di anotasi @TypeConverters
                    // Tambahkan TypeConverter yang disediakan secara manual
                    .addTypeConverter(Converters()) // <-- Ubah/Tambahkan ini
                    // .fallbackToDestructiveMigration() // Hanya untuk pengembangan, hapus di produksi
                    .build()
                    INSTANCE = instance
                    instance
            }
        }
    }
}