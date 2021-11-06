package com.regulus.dotaznik

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_firmy.*

class FirmyActivity : AppCompatActivity() {

    private lateinit var adapter: FirmyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firmy)

        title = getString(R.string.kontakty_vyberte_firmu)


        val firmy = resources.getStringArray(R.array.ica).toList()

        val callback: (String) -> Unit = {

            val t = Thread {
                val saver = Saver(this)
                val stranky = saver.get()

                stranky.kontakty.ico =
                    if(it.isEmpty())
                        ""
                    else
                        it.split(" - ")[1]
                stranky.kontakty.firma =
                    if(it.isEmpty())
                        ""
                    else
                        it.split(" - ")[0]


                saver.save(stranky)
            }

            t.start()
            t.join()

            finish()

        }


        rvFirmy.layoutManager = LinearLayoutManager(this)

        adapter = FirmyAdapter(firmy, this, callback)
        rvFirmy.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rvFirmy.addItemDecoration(dividerItemDecoration)

        etVyhledat.addTextChangedListener {
            adapter.filter(it.toString())
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()

        Thread {
            val saver = Saver(this)
            val stranky = saver.get()

            stranky.kontakty.firma = ""
            stranky.kontakty.ico = ""

            saver.save(stranky)
        }.start()
    }
}