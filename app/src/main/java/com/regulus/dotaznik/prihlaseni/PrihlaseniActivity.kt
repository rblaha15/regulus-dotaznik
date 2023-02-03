package com.regulus.dotaznik.prihlaseni

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.regulus.dotaznik.R
import com.regulus.dotaznik.Uzivatel
import com.regulus.dotaznik.databinding.ActivityPrihlaseniBinding
import com.regulus.dotaznik.prefsPrihlaseni
import kotlin.system.exitProcess


class PrihlaseniActivity : AppCompatActivity() {

    private var currentSelection: Uzivatel? = null
    private lateinit var zamestnanci: List<Uzivatel>
    private lateinit var zastupci: List<Uzivatel>

    private fun Int.toBoolean() = this == 1

    private lateinit var binding: ActivityPrihlaseniBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivityPrihlaseniBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        title = resources.getString(R.string.prihlaseni)

        val database = Firebase.database("https://lidi-c74ad-default-rtdb.europe-west1.firebasedatabase.app/")
        val lidiRef = database.getReference("lidi")

        lidiRef.get().addOnSuccessListener {

            val value2 = it.getValue<String>()!!

            setAdapters(value2.split("\n"))
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to read value.", it)

            Toast.makeText(this@PrihlaseniActivity, R.string.prihlaseni_potreba_internet, Toast.LENGTH_LONG).show()

            binding.btnZrusit.callOnClick()
        }


        val firmyRef = database.getReference("firmy")

        firmyRef.get().addOnSuccessListener {

            val value = it.value as List<*>

            prefsPrihlaseni.edit {
                putString("firmy", value.joinToString("\n"))
            }
        }

        val l = object : AdapterView.OnItemSelectedListener {

            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                currentSelection = null

                val vis = if (position == 0) View.GONE else View.VISIBLE

                binding.tvInfo.text = ""
                binding.tvInfo2.text = ""

                binding.tvInfo3.visibility = vis
                binding.etPrihlaseniEmail.visibility = vis
                binding.etPrihlaseniIco.visibility = vis
                binding.etPrihlaseniJmeno.visibility = vis
                binding.etPrihlaseniPrijmeni.visibility = vis

                if (position == 0) return

                val lidi = if (binding.rbJsem.isChecked) zamestnanci else zastupci

                currentSelection = lidi[position - 1]

                if (binding.rbJsem.isChecked) {
                    binding.tvInfo.text =
                        getString(R.string.prihlaseni_vybrany_jmeno_prijmeni, currentSelection!!.jmeno, currentSelection!!.prijmeni) +
                                getString(R.string.prihlaseni_vybrany_kod, currentSelection!!.cisloKo) +
                                getString(R.string.prihlaseni_vybrany_email, currentSelection!!.email)
                } else {
                    binding.tvInfo2.text = getString(R.string.prihlaseni_vybrany, currentSelection!!.jmeno, currentSelection!!.prijmeni)
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spZamestnanci.onItemSelectedListener = l
        binding.spZastupci.onItemSelectedListener = l

        binding.clJsem.visibility =  View.GONE
        binding.clNejsem.visibility =  View.VISIBLE

        binding.rgZamestanec.setOnCheckedChangeListener { _, _ ->
            currentSelection = null


            binding.clJsem.visibility = if (binding.rbJsem.isChecked) View.VISIBLE else View.GONE
            binding.clNejsem.visibility = if (binding.rbNejsem.isChecked) View.VISIBLE else View.GONE


            if (binding.rbJsem.isChecked) binding.spZamestnanci.setSelection(0) else binding.spZastupci.setSelection(0)

        }

        binding.btnOk.setOnClickListener {

            if (
                binding.etPrihlaseniJmeno.editText!!.text.toString() == binding.etPrihlaseniPrijmeni.editText!!.text.toString() &&
                binding.etPrihlaseniPrijmeni.editText!!.text.toString() == "admin" &&
                binding.etPrihlaseniIco.editText!!.text.toString() == "12345678"
            ) {
                admin()
                return@setOnClickListener
            }

            prefsPrihlaseni.edit {

                if (binding.rbJsem.isChecked) {
                    if (currentSelection == null) return@setOnClickListener

                    putString("jmeno", currentSelection!!.jmeno + " " + currentSelection!!.prijmeni)
                    putString("kod", currentSelection!!.cisloKo)
                    putString("email", currentSelection!!.email)
                    putString("ico", "")


                } else {

                    if (currentSelection == null) { Toast.makeText(this@PrihlaseniActivity,
                        R.string.je_potreba_zadat_zastupce, Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                    if (binding.etPrihlaseniEmail.editText!!.text.toString() == "") { Toast.makeText(this@PrihlaseniActivity,
                        R.string.je_potreba_zadat_email, Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                    if (binding.etPrihlaseniJmeno.editText!!.text.toString() == "") { Toast.makeText(this@PrihlaseniActivity,
                        R.string.je_potreba_zadat_jmeno, Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                    if (binding.etPrihlaseniPrijmeni.editText!!.text.toString() == "") { Toast.makeText(this@PrihlaseniActivity,
                        R.string.je_potreba_zadat_prijmeni, Toast.LENGTH_SHORT).show(); return@setOnClickListener }

                    putString("kod", currentSelection!!.cisloKo)
                    putString("ico", binding.etPrihlaseniIco.editText!!.text.toString())
                    putString("email", binding.etPrihlaseniEmail.editText!!.text.toString())
                    putString("jmeno", binding.etPrihlaseniJmeno.editText!!.text.toString() + " " + binding.etPrihlaseniPrijmeni.editText!!.text.toString())
                }

                putBoolean("prihlasen", true)

            }

            finish()
        }
        binding.btnZrusit.setOnClickListener {
            moveTaskToBack(true)
            exitProcess(-1)
        }

    }

    private fun setAdapters(stringy: List<String>) {
        zamestnanci = stringy.map {
            Uzivatel(
                email = it.split(";")[3],
                jmeno = it.split(";")[2],
                prijmeni = it.split(";")[1],
                cisloKo = it.split(";")[0],
                jeZastupce = it.split(";")[4].toInt().toBoolean()
            )
        }

        zastupci = zamestnanci.filter { it.jeZastupce }

        val zamestnanciJmena = listOf(getString(R.string.vyberte)) +
                zamestnanci.map { "${it.jmeno} ${it.prijmeni}" }

        val zastupciJmena = listOf(getString(R.string.vyberte)) +
                zastupci.map { "${it.jmeno} ${it.prijmeni}" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, zamestnanciJmena)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, zastupciJmena)

        binding.spZamestnanci.adapter = adapter
        binding.spZastupci.adapter = adapter2
    }


    private fun admin() {

    }

}