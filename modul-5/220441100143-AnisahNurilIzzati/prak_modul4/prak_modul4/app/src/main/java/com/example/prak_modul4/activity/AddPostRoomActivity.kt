package com.example.prak_modul4.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.features.registerImagePicker
import com.example.prak_modul4.MainActivity
import com.example.prak_modul4.R
import com.example.prak_modul4.room.PostDatabase
import com.example.prak_modul4.room.PostViewModel
import com.example.prak_modul4.room.PostViewModelFactory
import com.example.prak_modul4.utils.reduceFileImage
import com.example.prak_modul4.utils.uriToFile
import com.google.android.material.textfield.TextInputEditText


class AddPostRoomActivity : AppCompatActivity() {

    // Mendeklarasikan variabel untuk menyimpan URI gambar yang dipilih
    private var currentImageUri: Uri? = null
    // Mendeklarasikan ImageView untuk menampilkan gambar yang dipilih
    private lateinit var vPostImage: ImageView
    // Mendeklarasikan ViewModel untuk interaksi dengan database
    private lateinit var postViewModel: PostViewModel
    private lateinit var vPostTitle: TextInputEditText
    // Mendeklarasikan EditText untuk input deskripsi pemain
    private lateinit var vPostDesc: TextInputEditText
    // Mendeklarasikan EditText untuk input gambar pemain
    private lateinit var vText_img: TextView

    // Mendeklarasikan image picker untuk memilih gambar dari galeri
    private val imagePickerLauncher = registerImagePicker {
        val firstImage = it.firstOrNull() ?: return@registerImagePicker
        if (firstImage.uri.toString().isNotEmpty()) {
            // Menampilkan ImageView jika gambar berhasil dipilih
            vPostImage.visibility = View.VISIBLE
            // Menyimpan URI gambar yang dipilih
            currentImageUri = firstImage.uri
            // Menampilkan pesan bahwa gambar berhasil dimasukkan
            vText_img.setText("change")

            // Menggunakan library Glide untuk menampilkan gambar yang dipilih
            Glide.with(vPostImage)
                .load(firstImage.uri)
                .into(vPostImage)
        } else {
            // Menyembunyikan ImageView jika tidak ada gambar yang dipilih
            View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post_room)
        val factory = PostViewModelFactory.getInstance(this) //ini
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java] //ini
        vPostTitle = findViewById(R.id.post_title_edit)
        vPostImage = findViewById(R.id.post_img_edit)
        vPostDesc = findViewById(R.id.post_desc_edit)
        vText_img = findViewById(R.id.text_img)
        onClick()
    }


    private fun onClick() {
        val openImagePicker = findViewById<ImageView>(R.id.post_img_edit)
        openImagePicker.setOnClickListener {
            imagePickerLauncher.launch(
                ImagePickerConfig {
                    mode = ImagePickerMode.SINGLE
                    returnMode = ReturnMode.ALL
                    isFolderMode = true
                    folderTitle = "Galeri"
                    isShowCamera = false
                    imageTitle = "Click to choice the image"
                    doneButtonText = "Done"
                }
            )
        }

        val btnSavedPlayer = findViewById<Button>(R.id.btn_savedPost)
        btnSavedPlayer.setOnClickListener {
            if (validateInput()) {
                savedData()
            }
        }
    }

    private fun validateInput(): Boolean {
        var error = 0
        if (vPostTitle.text.toString().isEmpty()){
            error++
            vPostTitle.error = "Title is not empty!"
        }

        if (vPostDesc.text.toString().isEmpty()) {
            error++
            vPostDesc.error = "Desc is not empty!"
        }
        if (vText_img.text.toString() == "add") {
            error++
            vText_img.error = "Image is not Empty!"
        }

        return error == 0
    }


    private fun savedData() {
        val imageFile = currentImageUri?.let { uriToFile(it, this).reduceFileImage() }


        val post = imageFile?.let {
            val descriptionText = vPostDesc.text.toString()

            PostDatabase(
                id = 0,
                name = vPostTitle.text.toString(),
                description = descriptionText,
                image = imageFile,
                like = 0


            )
        }

        if (post != null) postViewModel.insertPost(post)

        Toast.makeText(
            this@AddPostRoomActivity,
            "Data Success Added",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    fun toMain(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}