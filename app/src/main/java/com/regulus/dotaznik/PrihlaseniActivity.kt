package com.regulus.dotaznik

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_prihlaseni.*
import kotlinx.android.synthetic.main.fragment_kontakty.*
import kotlin.system.exitProcess


class PrihlaseniActivity : AppCompatActivity() {

    private var currentSelection: Clovek? = null
    private lateinit var zamestnanci: MutableList<Clovek>
    private lateinit var zastupci: MutableList<Clovek>


    private fun Int.toBoolean() = this == 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prihlaseni)

        title = resources.getString(R.string.prihlaseni)


        val database = Firebase.database("https://lidi-c74ad-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("lidi")

        myRef.get().addOnSuccessListener {

            val value2 = it.getValue<String>()!!

            Log.d("Firebase", value2)

            etZadat?.setText(value2)

            tvZkontrolovat?.text = value2


            setAdapters(value2.split("\n"))
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to read value.", it)

            Toast.makeText(this@PrihlaseniActivity, R.string.prihlaseni_potreba_internet, Toast.LENGTH_LONG).show()

            btnZrusit.callOnClick()
        }


        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return

                val value2 = dataSnapshot.getValue<String>()!!


                Log.d("Firebase", value2)

                etZadat?.setText(value2)

                tvZkontrolovat?.text = value2


                setAdapters(value2.split("\n"))

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase", "Failed to read value.", error.toException())


            }
        })



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

                tvInfo.text = ""
                tvInfo2.text = ""

                etPrihlaseniEmail.visibility = vis
                etPrihlaseniIco.visibility = vis
                etPrihlaseniJmeno.visibility = vis
                etPrihlaseniPrijmeni.visibility = vis
                tvPrihlaseniEmail.visibility = vis
                tvPrihlaseniIco.visibility = vis
                tvPrihlaseniJmeno.visibility = vis
                tvPrihlaseniPrijmeni.visibility = vis

                if (position == 0) return



                val lidi = if (rbJsem.isChecked) zamestnanci else zastupci

                currentSelection = lidi[position - 1]



                if (rbJsem.isChecked) {
                    tvInfo.text = getString(R.string.prihlaseni_vybrany_jmeno_prijmeni, currentSelection!!.jmeno, currentSelection!!.prijmeni) +
                            getString(R.string.prihlaseni_vybrany_kod, currentSelection!!.cislo_ko) +
                            getString(R.string.prihlaseni_vybrany_email, currentSelection!!.email)
                } else {
                    tvInfo2.text = getString(R.string.prihlaseni_vybrany, currentSelection!!.jmeno, currentSelection!!.prijmeni)
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spZamestnanci.onItemSelectedListener = l
        spZastupci.onItemSelectedListener = l





        clJsem.visibility =  View.GONE
        clNejsem.visibility =  View.VISIBLE

        rgZamestanec.setOnCheckedChangeListener { _, _ ->
            currentSelection = null


            clJsem.visibility = if (rbJsem.isChecked) View.VISIBLE else View.GONE
            clNejsem.visibility = if (rbNejsem.isChecked) View.VISIBLE else View.GONE


            if (rbJsem.isChecked) spZamestnanci.setSelection(0) else spZastupci.setSelection(0)

        }


        btnOk.setOnClickListener {



            if (etPrihlaseniJmeno.text.toString() == etPrihlaseniPrijmeni.text.toString() &&
                    etPrihlaseniPrijmeni.text.toString() == "admin" &&
                    etPrihlaseniIco.text.toString() == "12345678") { admin(); return@setOnClickListener }

            val sharedPref = this.getSharedPreferences("PREFS_PRIHLASENI", Context.MODE_PRIVATE)

            sharedPref.edit().apply {

                if (rbJsem.isChecked) {
                    if (currentSelection == null) return@setOnClickListener



                    putString("jmeno", currentSelection!!.jmeno + " " + currentSelection!!.prijmeni)
                    putString("kod", currentSelection!!.cislo_ko)
                    putString("email", currentSelection!!.email)
                    putString("ico", "")


                } else {

                    if (currentSelection == null) { Toast.makeText(this@PrihlaseniActivity, R.string.je_potreba_zadat_zastupce, Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                    if (etPrihlaseniEmail.text.toString() == "") { Toast.makeText(this@PrihlaseniActivity, R.string.je_potreba_zadat_email, Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                    if (etPrihlaseniJmeno.text.toString() == "") { Toast.makeText(this@PrihlaseniActivity, R.string.je_potreba_zadat_jmeno, Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                    if (etPrihlaseniPrijmeni.text.toString() == "") { Toast.makeText(this@PrihlaseniActivity, R.string.je_potreba_zadat_prijmeni, Toast.LENGTH_SHORT).show(); return@setOnClickListener }

                    putString("kod", currentSelection!!.cislo_ko)
                    putString("ico", etPrihlaseniIco.text.toString())
                    putString("email", etPrihlaseniEmail.text.toString())
                    putString("jmeno", etPrihlaseniJmeno.text.toString() + " " + etPrihlaseniPrijmeni.text.toString())
                }

                putBoolean("prihlasen", true)

                apply()
            }

            finish()
        }
        btnZrusit.setOnClickListener {
            moveTaskToBack(true)
            exitProcess(-1)
        }

    }

    private fun setAdapters(stringy: List<String>) {
        zamestnanci = mutableListOf()
        zastupci = mutableListOf()

        val zamestnanciJmena = mutableListOf(resources.getString(R.string.vyberte))
        val zastupciJmena = mutableListOf(resources.getString(R.string.vyberte))


        for (i in stringy.indices) {
            zamestnanci.add(Clovek())

            stringy[i].split(";").apply {
                zamestnanci[i].cislo_ko = this[0]
                zamestnanci[i].prijmeni = this[1]
                zamestnanci[i].jmeno = this[2]
                zamestnanci[i].email = this[3]
                zamestnanci[i].jeZastupce = this[4].toInt().toBoolean()
            }

            zamestnanci[i].apply {zamestnanciJmena.add("$jmeno $prijmeni")}



            if (zamestnanci[i].jeZastupce) {
                zastupci.add(zamestnanci[i])

                zastupci.last().apply {zastupciJmena.add("$jmeno $prijmeni")}
            }
        }


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, zamestnanciJmena)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, zastupciJmena)

        spZamestnanci.adapter = adapter
        spZastupci.adapter = adapter2
    }


    private fun admin() {

        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
    }

}