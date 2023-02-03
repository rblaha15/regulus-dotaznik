package com.regulus.dotaznik.vybiratorFirem

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.regulus.dotaznik.R
import com.regulus.dotaznik.databinding.ActivityFirmyBinding
import com.regulus.dotaznik.prefsPrihlaseni
import com.regulus.dotaznik.saver

class FirmyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirmyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivityFirmyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.topAppBar)

        title = getString(R.string.vyberte_firmu)

        binding.rvFirmy.layoutManager = LinearLayoutManager(this)

        binding.rvFirmy.adapter = FirmyAdapter(this) {

            val stranky = saver.get()

            stranky.kontakty.ico =
                if (it.isEmpty())
                    ""
                else
                    it.split(" – ")[1]
            stranky.kontakty.firma =
                if (it.isEmpty())
                    ""
                else
                    it.split(" – ")[0]


            saver.save(stranky)


            finish()

        }

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.rvFirmy.addItemDecoration(dividerItemDecoration)

        binding.etVyhledat.addTextChangedListener {
            (binding.rvFirmy.adapter as FirmyAdapter).filter(it.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.firmy_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.actionAktualizovat -> {

                val database = Firebase.database("https://lidi-c74ad-default-rtdb.europe-west1.firebasedatabase.app/")
                val myRef = database.getReference("firmy")

                myRef.get().addOnSuccessListener {

                    val value = it.value as List<*>

                    prefsPrihlaseni.edit {
                        putString("firmy", value.joinToString("\n"))
                    }

                    (binding.rvFirmy.adapter as FirmyAdapter).aktualizovat()

                    (binding.rvFirmy.adapter as FirmyAdapter).filter(binding.etVyhledat.text.toString())
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val stranky = saver.get()

        stranky.kontakty.firma = ""
        stranky.kontakty.ico = ""

        saver.save(stranky)
    }
}
