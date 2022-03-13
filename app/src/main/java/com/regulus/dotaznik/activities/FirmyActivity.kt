package com.regulus.dotaznik.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.regulus.dotaznik.R
import com.regulus.dotaznik.adapters.FirmyAdapter
import com.regulus.dotaznik.databinding.ActivityFirmyBinding
import com.regulus.dotaznik.saver

class FirmyActivity : AppCompatActivity() {

    private lateinit var adapter: FirmyAdapter

    private lateinit var binding: ActivityFirmyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirmyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        title = getString(R.string.kontakty_vyberte_firmu)


        val firmy = resources.getStringArray(R.array.ica).toList()

        val callback: (String) -> Unit = {

            val stranky = saver.get()

            stranky.kontakty.ico =
                if(it.isEmpty())
                    ""
                else
                    it.split(" – ")[1]
            stranky.kontakty.firma =
                if(it.isEmpty())
                    ""
                else
                    it.split(" – ")[0]


            saver.save(stranky)


            finish()

        }

        binding.rvFirmy.layoutManager = LinearLayoutManager(this)

        adapter = FirmyAdapter(firmy, this, callback)
        binding.rvFirmy.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.rvFirmy.addItemDecoration(dividerItemDecoration)

        binding.etVyhledat.addTextChangedListener {
            adapter.filter(it.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val stranky = saver.get()

        stranky.kontakty.firma = ""
        stranky.kontakty.ico = ""

        saver.save(stranky)
    }
}