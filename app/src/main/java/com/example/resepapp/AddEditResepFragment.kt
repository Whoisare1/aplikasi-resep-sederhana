package com.example.resepapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.resepapp.data.Resep
import com.example.resepapp.databinding.FragmentAddEditResepBinding
import com.example.resepapp.viewmodel.ResepViewModel
import com.example.resepapp.viewmodel.ResepViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddEditResepFragment : Fragment() {

    private var _binding: FragmentAddEditResepBinding? = null
    private val binding get() = _binding!!

    private val resepViewModel: ResepViewModel by activityViewModels {
        ResepViewModelFactory((activity?.application as ResepApplication).repository)
    }

    // Untuk menerima argumen resepId jika dalam mode edit
    private val args: AddEditResepFragmentArgs by navArgs()
    private var currentResepId: Int = -1
    private var currentGambarPath: String? = null

    // Activity Result Launcher untuk memilih gambar
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    saveImageToInternalStorage(uri)?.let { savedPath ->
                        currentGambarPath = savedPath
                        binding.imgResepPreview.load(File(savedPath)) {
                            placeholder(R.drawable.ic_image_placeholder)
                            error(R.drawable.ic_image_placeholder)
                        }
                    } ?: Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditResepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentResepId = args.resepId // Mengambil resepId dari argumen
        if (currentResepId != -1) {
            // Mode Edit: Muat data resep yang sudah ada
            resepViewModel.getResepById(currentResepId).observe(viewLifecycleOwner) { resep ->
                resep?.let {
                    binding.etNamaResep.setText(it.nama)
                    binding.etDeskripsiSingkat.setText(it.deskripsiSingkat)
                    binding.etDeskripsiLengkap.setText(it.deskripsiLengkap)
                    currentGambarPath = it.gambarPath
                    if (!it.gambarPath.isNullOrEmpty()) {
                        binding.imgResepPreview.load(File(it.gambarPath)) {
                            placeholder(R.drawable.ic_image_placeholder)
                            error(R.drawable.ic_image_placeholder)
                        }
                    }

                    // Isi bahan-bahan
                    it.bahan.forEach { bahanItem ->
                        addEditText(binding.llBahanContainer, bahanItem)
                    }
                    if (it.bahan.isEmpty()) { // Tambahkan satu EditText kosong jika belum ada bahan
                        addEditText(binding.llBahanContainer, "")
                    }

                    // Isi langkah-langkah
                    it.langkahLangkah.forEach { langkahItem ->
                        addEditText(binding.llLangkahContainer, langkahItem)
                    }
                    if (it.langkahLangkah.isEmpty()) { // Tambahkan satu EditText kosong jika belum ada langkah
                        addEditText(binding.llLangkahContainer, "")
                    }
                }
            }
        } else {
            // Mode Tambah: Tambahkan satu EditText kosong untuk bahan dan langkah
            addEditText(binding.llBahanContainer, "")
            addEditText(binding.llLangkahContainer, "")
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnAddBahan.setOnClickListener {
            addEditText(binding.llBahanContainer, "")
        }

        binding.btnAddLangkah.setOnClickListener {
            addEditText(binding.llLangkahContainer, "")
        }

        binding.btnPickImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnSimpanResep.setOnClickListener {
            saveResep()
        }
    }

    private fun addEditText(container: LinearLayout, text: String) {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_dynamic_edittext, container, false)
        val editText = view.findViewById<EditText>(R.id.et_dynamic_item)
        val deleteButton = view.findViewById<View>(R.id.btn_delete_dynamic_item)

        editText.setText(text)
        editText.hint = if (container.id == R.id.ll_bahan_container) "Bahan..." else "Langkah..."

        deleteButton.setOnClickListener {
            container.removeView(view)
        }
        container.addView(view)
    }

    private fun getEditTextsContent(container: LinearLayout): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            val editText = view.findViewById<EditText>(R.id.et_dynamic_item)
            val text = editText.text.toString().trim()
            if (text.isNotEmpty()) {
                list.add(text)
            }
        }
        return list
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        val context = requireContext()
        val fileName = "resep_image_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName) // Simpan di direktori internal app

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveResep() {
        val nama = binding.etNamaResep.text.toString().trim()
        val deskripsiSingkat = binding.etDeskripsiSingkat.text.toString().trim()
        val deskripsiLengkap = binding.etDeskripsiLengkap.text.toString().trim()
        val bahan = getEditTextsContent(binding.llBahanContainer)
        val langkahLangkah = getEditTextsContent(binding.llLangkahContainer)

        if (nama.isEmpty() || deskripsiSingkat.isEmpty() || deskripsiLengkap.isEmpty() || bahan.isEmpty() || langkahLangkah.isEmpty()) {
            Toast.makeText(requireContext(), "Semua kolom (kecuali gambar) harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val resep = Resep(
            id = if (currentResepId != -1) currentResepId else 0,
            nama = nama,
            deskripsiSingkat = deskripsiSingkat,
            deskripsiLengkap = deskripsiLengkap,
            bahan = bahan,
            langkahLangkah = langkahLangkah,
            gambarPath = currentGambarPath
        )

        if (currentResepId != -1) {
            resepViewModel.update(resep)
            Toast.makeText(requireContext(), "Resep berhasil diperbarui", Toast.LENGTH_SHORT).show()
        } else {
            resepViewModel.insert(resep)
            Toast.makeText(requireContext(), "Resep berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack() // Kembali ke daftar resep
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}