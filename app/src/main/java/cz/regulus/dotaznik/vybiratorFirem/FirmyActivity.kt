package cz.regulus.dotaznik.vybiratorFirem

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.regulus.dotaznik.R
import com.regulus.dotaznik.databinding.ActivityFirmyBinding
import cz.regulus.dotaznik.Stranky
import cz.regulus.dotaznik.prefsPrihlaseni
import cz.regulus.dotaznik.saver

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

            var stranky = saver.get()

            stranky = stranky.copy(
                kontakty = (stranky.kontakty as Stranky.Stranka.Kontakty).copy(
                    icoMontazniFirmy = ((stranky.kontakty as Stranky.Stranka.Kontakty).icoMontazniFirmy as Stranky.Stranka.Kontakty.IcoMontazniFirmy).copy(
                        text = if (it.isEmpty())
                            ""
                        else
                            it.split(" – ")[1]
                    )
                )
            )

//            stranky.kontakty.firma =
//                if (it.isEmpty())
//                    ""
//                else
//                    it.split(" – ")[0]

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

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        var stranky = saver.get()

        stranky = stranky.copy(
            kontakty = (stranky.kontakty as Stranky.Stranka.Kontakty).copy(
                icoMontazniFirmy = ((stranky.kontakty as Stranky.Stranka.Kontakty).icoMontazniFirmy as Stranky.Stranka.Kontakty.IcoMontazniFirmy).copy(
                    text = ""
                )
            )
        )

        saver.save(stranky)
    }
}
