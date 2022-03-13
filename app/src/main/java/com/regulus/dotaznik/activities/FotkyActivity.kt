package com.regulus.dotaznik.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.regulus.dotaznik.R
import com.regulus.dotaznik.adapters.FotkyAdapter
import com.regulus.dotaznik.databinding.ActivityFotkyBinding
import java.io.File
import java.io.FileNotFoundException


class FotkyActivity : AppCompatActivity() {

    private lateinit var adapter: FotkyAdapter
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    private lateinit var getMultipleContents: ActivityResultLauncher<String>

    private lateinit var binding: ActivityFotkyBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotkyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.topAppBarFotky)

        title = getString(R.string.fotky_sprava_fotek)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val sharedPref = getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)

        //sharedPref.edit().putInt("fotky", 0).apply()


        takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.d("foto", it.toString())


            val i = sharedPref.getInt("fotky", 0)


            adapter.notifyItemInserted(i+1)
            adapter.notifyDataSetChanged()

            sharedPref.edit().putInt("fotky", i+1).apply()

        }

        getMultipleContents = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            Log.d("foto", it.toString())

            it.forEach { uri ->

                Log.d("foto", uri.toString())

                val i = sharedPref.getInt("fotky", 0)

                if (i >= 5) {
                    Toast.makeText(this, R.string.fotky_maximalne_fotek,Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }

                try {

                    val outputStream = contentResolver.openInputStream(uri)!!
                    val file = File(filesDir, "photo${i + 1}.jpg")

                    outputStream.copyTo(file.outputStream())

                } catch (e: FileNotFoundException) {

                    MaterialAlertDialogBuilder(this).apply {
                        setTitle("Něco se pokazilo!")

                        setMessage("Podrobnější informace:\n\n$i\n\n${e.stackTraceToString()}\n\n$filesDir")
                    }

                    return@registerForActivityResult
                }

                adapter.notifyItemInserted(i + 1)
                adapter.notifyDataSetChanged()

                sharedPref.edit().putInt("fotky", i + 1).apply()


                Log.d("foto", uri.toString())
            }
        }


        binding.fabVyfotit.setOnClickListener {

            val i = sharedPref.getInt("fotky", 0)

            if (i >= 5) {
                Toast.makeText(this, R.string.fotky_maximalne_fotek,Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val iHavePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            else
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED


            if (!iHavePermission) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 2)
                else
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),2)

            } else {

                val file = File(filesDir, "photo${i+1}.jpg")
                val imageUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)

                takePicture.launch(imageUri)

            }
        }


        binding.fabVybrat.setOnClickListener {

            val i = sharedPref.getInt("fotky", 0)

            if (i >= 5) {
                Toast.makeText(this, R.string.fotky_maximalne_fotek,Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            getMultipleContents.launch("image/*")

        }

        adapter = FotkyAdapter(this)


        binding.rvFotky.layoutManager = LinearLayoutManager(this)
        binding.rvFotky.adapter = adapter


        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.rvFotky.addItemDecoration(dividerItemDecoration)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != 2 || grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED || permissions[0] != Manifest.permission.CAMERA) return

        val sharedPref = getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)



        val i = sharedPref.getInt("fotky", 0)
        val file = File(filesDir, "photo${i+1}.jpg")
        val imageUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)

        takePicture.launch(imageUri)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {

                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}