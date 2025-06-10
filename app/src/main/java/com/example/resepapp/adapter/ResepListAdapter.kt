package com.example.resepapp.adapter

import android.util.Log // Import Log untuk debugging
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load // Import Coil
import com.example.resepapp.R
import com.example.resepapp.data.Resep
import com.example.resepapp.databinding.ItemResepBinding
import java.io.File // Import File untuk Coil

class ResepListAdapter(
    private val onItemClick: (Resep) -> Unit,
    private val  onItemLongClick: (Resep) -> Boolean,
    private val onDeleteClick: (Resep) -> Unit,
    private val onEditClick: (Resep) -> Unit
) : ListAdapter<Resep, ResepListAdapter.ResepViewHolder>(ResepComparator()) {

    var inSelectionMode: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResepViewHolder {
        val binding = ItemResepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResepViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class ResepViewHolder(private val binding: ItemResepBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(resep: Resep) {
            binding.apply {
                tvNamaResep.text = resep.nama
                tvDeskripsiSingkat.text = resep.deskripsiSingkat

                // --- BAGIAN INI YANG PERLU DIUBAH ---
                if (!resep.gambarPath.isNullOrEmpty()) {
                    val imageFile = File(resep.gambarPath)
                    if (imageFile.exists()) {
                        Log.d("ResepAdapter", "Loading image for ${resep.nama} from: ${resep.gambarPath}") // Log untuk debugging
                        imgResepThumbnail.load(imageFile) {
                            placeholder(R.drawable.ic_image_placeholder) // Tampilkan placeholder saat memuat
                            error(R.drawable.ic_image_placeholder)       // Tampilkan placeholder jika gagal
                        }
                    } else {
                        Log.e("ResepAdapter", "Image file NOT found for ${resep.nama} at: ${resep.gambarPath}") // Log error jika file tidak ditemukan
                        imgResepThumbnail.setImageResource(R.drawable.ic_image_placeholder) // Kembali ke placeholder
                    }
                } else {
                    Log.d("ResepAdapter", "No image path for resep: ${resep.nama}") // Log jika tidak ada path gambar
                    imgResepThumbnail.setImageResource(R.drawable.ic_image_placeholder) // Tampilkan placeholder default
                }
                // --- AKHIR BAGIAN YANG DIUBAH ---

                btnDeleteResep.visibility = if (inSelectionMode) View.VISIBLE else View.GONE
                btnEditResep.visibility = if (inSelectionMode) View.VISIBLE else View.GONE

                root.setOnClickListener {
                    if (inSelectionMode) {
                        onEditClick(resep)
                    } else {
                        onItemClick(resep)
                    }
                    setSelectionMode(false)
                }

                root.setOnLongClickListener {
                    setSelectionMode(!inSelectionMode)
                    true
                }

                btnDeleteResep.setOnClickListener {
                    onDeleteClick(resep)
                    setSelectionMode(false)
                }

                btnEditResep.setOnClickListener {
                    onEditClick(resep)
                    setSelectionMode(false)
                }
            }
        }
    }

    fun setSelectionMode(enabled: Boolean) {
        if (inSelectionMode != enabled) {
            inSelectionMode = enabled
            notifyDataSetChanged()
        }
    }

    class ResepComparator : DiffUtil.ItemCallback<Resep>() {
        override fun areItemsTheSame(oldItem: Resep, newItem: Resep): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Resep, newItem: Resep): Boolean {
            return oldItem == newItem
        }
    }
}