package com.regulus.dotaznik.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.regulus.dotaznik.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val database = Firebase.database("https://lidi-c74ad-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("lidi")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue<String>()

                binding.etZadat.setText(value)

                binding.tvZkontrolovat.text = value

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Failed to read value.", error.toException())
            }
        })



        binding.btnOdeslat.setOnClickListener {

            myRef.setValue(binding.etZadat.text.toString())
        }
    }
}