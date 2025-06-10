package com.example.resepapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.resepapp.databinding.FragmentDetailResepBinding
import com.example.resepapp.viewmodel.ResepViewModel
import com.example.resepapp.viewmodel.ResepViewModelFactory
import java.io.File

class DetailResepFragment : Fragment() {

    private var _binding: FragmentDetailResepBinding? = null
    private val binding get() = _binding!!

    private val resepViewModel: ResepViewModel by activityViewModels {
        ResepViewModelFactory((activity?.application as ResepApplication).repository)
    }

    private val args: DetailResepFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailResepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resepId = args.resepId // Mengambil resepId dari argumen
        resepViewModel.getResepById(resepId).observe(viewLifecycleOwner) { resep ->
            resep?.let {
                binding.tvDetailNamaResep.text = it.nama
                binding.tvDetailDeskripsiSingkat.text = it.deskripsiSingkat
                binding.tvDetailDeskripsiLengkap.text = it.deskripsiLengkap

                // Tampilkan bahan-bahan sebagai bullet points
                binding.tvDetailBahan.text = it.bahan.joinToString("\n") { bahan -> "• $bahan" }

                // Tampilkan langkah-langkah sebagai numbered list
                binding.tvDetailLangkah.text = it.langkahLangkah.mapIndexed { index, langkah ->
                    "${index + 1}. $langkah"
                }.joinToString("\n")

                // Muat gambar jika ada
                if (!it.gambarPath.isNullOrEmpty()) {
                    binding.imgDetailResep.load(File(it.gambarPath)) {
                        placeholder(R.drawable.ic_image_placeholder)
                        error(R.drawable.ic_image_placeholder)
                    }
                } else {
                    binding.imgDetailResep.setImageResource(R.drawable.ic_image_placeholder)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}