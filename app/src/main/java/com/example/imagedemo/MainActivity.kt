package com.example.imagedemo

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.imagedemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var startActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val bitmap = (result.data?.extras?.get("data") as? Bitmap)
                    ?: return@registerForActivityResult
                binding.imageView.setImageBitmap(bitmap)
            }
        }

        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                binding.imageView.setImageBitmap(bitmap)
            }
        }
        
        binding.imageButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Image Selector")
        dialogBuilder.setMessage("Dialog Message")
        dialogBuilder.setPositiveButton("Camera") { dialog, _ ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityLauncher.launch(intent)
            }
        }
        dialogBuilder.setNegativeButton("Gallery") { dialog, _ ->

        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}