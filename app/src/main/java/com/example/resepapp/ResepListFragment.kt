package com.example.resepapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resepapp.adapter.ResepListAdapter
import com.example.resepapp.data.Resep
import com.example.resepapp.databinding.FragmentResepListBinding
import com.example.resepapp.viewmodel.ResepViewModel
import com.example.resepapp.viewmodel.ResepViewModelFactory

class ResepListFragment : Fragment() {

    private var _binding: FragmentResepListBinding? = null
    private val binding get() = _binding!!

    private val resepViewModel: ResepViewModel by activityViewModels {
        ResepViewModelFactory((activity?.application as ResepApplication).repository)
    }

    private lateinit var resepListAdapter: ResepListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResepListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeResepData()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        resepListAdapter = ResepListAdapter(
            onItemClick = { resep ->
                // Logika ini hanya akan terpicu jika BUKAN dalam mode seleksi (klik normal untuk detail)
                if (!resepListAdapter.inSelectionMode) {
                    val action = ResepListFragmentDirections.actionResepListFragmentToDetailResepFragment(resep.id)
                    findNavController().navigate(action)
                }
                // Jika dalam mode seleksi, onItemClick akan dipanggil dari adapter untuk edit,
                // jadi tidak perlu navigasi detail di sini.
            },
            onItemLongClick = { resep ->
                // Toggle mode seleksi saat long click
                resepListAdapter.setSelectionMode(!resepListAdapter.inSelectionMode)
                true // Mengonsumsi event long click
            },
            onDeleteClick = { resep ->
                showDeleteConfirmationDialog(resep)
            },
            onEditClick = { resep -> // <-- Implementasi listener edit
                val action = ResepListFragmentDirections.actionResepListFragmentToAddEditResepFragment(resep.id)
                findNavController().navigate(action)
            }
        )

        binding.recyclerViewResep.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resepListAdapter
        }
    }

    private fun observeResepData() {
        resepViewModel.allResep.observe(viewLifecycleOwner) { resepList ->
            resepList?.let {
                resepListAdapter.submitList(it)
                binding.tvNoData.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerViewResep.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                // Reset mode seleksi jika daftar kosong atau berubah
                if (resepListAdapter.inSelectionMode && it.isEmpty()) {
                    resepListAdapter.setSelectionMode(false)
                }
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    resepViewModel.searchResep(it).observe(viewLifecycleOwner) { searchResults ->
                        resepListAdapter.submitList(searchResults)
                        binding.tvNoData.visibility = if (searchResults.isEmpty()) View.VISIBLE else View.GONE
                        binding.recyclerViewResep.visibility = if (searchResults.isEmpty()) View.GONE else View.VISIBLE
                        // Reset mode seleksi saat pencarian berubah
                        resepListAdapter.setSelectionMode(false)
                    }
                }
                return true
            }
        })
    }

    private fun showDeleteConfirmationDialog(resep: Resep) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Resep")
            .setMessage("Apakah Anda yakin ingin menghapus resep '${resep.nama}'?")
            .setPositiveButton("Hapus") { dialog, _ ->
                resepViewModel.delete(resep)
                dialog.dismiss()
                resepListAdapter.setSelectionMode(false) // Keluar dari mode seleksi setelah penghapusan
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}