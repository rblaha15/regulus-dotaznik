package com.regulus.dotaznik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_bazen.*
import java.util.*


class BazenFragment : Fragment() {

    private val timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bazen, container, false)

        x(view)

        return view
    }
    
    private fun x(view: View) {

        val adapter1 = ArrayAdapter.createFromResource(requireActivity(), R.array.dobaVyuzivani, android.R.layout.simple_spinner_item)
        val adapter2 = ArrayAdapter.createFromResource(requireActivity(), R.array.umisteni, android.R.layout.simple_spinner_item)
        val adapter3 = ArrayAdapter.createFromResource(requireActivity(), R.array.druhVody, android.R.layout.simple_spinner_item)
        val adapter4 = ArrayAdapter.createFromResource(requireActivity(), R.array.tvar, android.R.layout.simple_spinner_item)
        val adapter5 = ArrayAdapter.createFromResource(requireActivity(), R.array.zakryti, android.R.layout.simple_spinner_item)

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.findViewById<Spinner>(R.id.spDoba).adapter = adapter1
        view.findViewById<Spinner>(R.id.spUmisteni).adapter = adapter2
        view.findViewById<Spinner>(R.id.spDruhVody).adapter = adapter3
        view.findViewById<Spinner>(R.id.spTvar).adapter = adapter4
        view.findViewById<Spinner>(R.id.spZakryti).adapter = adapter5


        update(view)


        view.findViewById<Spinner>(R.id.spTvar).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    update()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        Thread {

            val task = object : TimerTask() {
                override fun run() {

                    val saver = Saver(requireActivity())
                    val stranky = saver.get()

                    stranky.bazen.apply {
                        dobaPos = spDoba.selectedItemPosition
                        doba = spDoba.selectedItem.toString()
                        umisteniPos = spUmisteni.selectedItemPosition
                        umisteni = spUmisteni.selectedItem.toString()
                        druhVodyPos = spDruhVody.selectedItemPosition
                        druhVody = spDruhVody.selectedItem.toString()
                        tvarPos = spTvar.selectedItemPosition
                        tvar = spTvar.selectedItem.toString()
                        delka = etDelka.text.toString()
                        sirka = etSirka.text.toString()
                        prumer = etPrumer.text.toString()
                        hloubka = etHloubka.text.toString()
                        zakrytiPos = spZakryti.selectedItemPosition
                        zakryti = spZakryti.selectedItem.toString()
                        teplota = etTeplota.text.toString()
                        poznamka = etPoznamka4.text.toString()
                    }


                    saver.save(stranky)
                }
            }


            timer.scheduleAtFixedRate(task, 0, 200)


            val saver = Saver(requireActivity())
            val stranky = saver.get()


            requireActivity().runOnUiThread {

                spDoba.setSelection(stranky.bazen.dobaPos)
                spUmisteni.setSelection(stranky.bazen.umisteniPos)
                spDruhVody.setSelection(stranky.bazen.druhVodyPos)
                spTvar.setSelection(stranky.bazen.tvarPos)
                etDelka.setText(stranky.bazen.delka)
                etSirka.setText(stranky.bazen.sirka)
                etPrumer.setText(stranky.bazen.prumer)
                etHloubka.setText(stranky.bazen.hloubka)
                spZakryti.setSelection(stranky.bazen.zakrytiPos)
                etTeplota.setText(stranky.bazen.teplota)
                etPoznamka4.setText(stranky.bazen.poznamka)

            }


        }.start()
        
    }


    private fun update(view: View = requireView()) {


        val spTvar: Spinner = view.findViewById(R.id.spTvar)

        val etDelka: EditText = view.findViewById(R.id.etDelka)
        val etSirka: EditText = view.findViewById(R.id.etSirka)
        val etPrumer: EditText = view.findViewById(R.id.etPrumer)

        val tvDelka: TextView = view.findViewById(R.id.tvDelka)
        val tvSirka: TextView = view.findViewById(R.id.tvSirka)
        val tvPrumer: TextView = view.findViewById(R.id.tvPrumer)

        val tvDelkaJednotky: TextView = view.findViewById(R.id.tvDelkaJednotky)
        val tvSirkaJednotky: TextView = view.findViewById(R.id.tvSirkaJednotky)
        val tvPrumerJednotky: TextView = view.findViewById(R.id.tvPrumerJednotky)

        etDelka.visibility = if (spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        etSirka.visibility = if (spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        etPrumer.visibility = if (spTvar.selectedItemPosition == 2) View.VISIBLE else View.GONE

        tvDelka.visibility = if (spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        tvSirka.visibility = if (spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        tvPrumer.visibility = if (spTvar.selectedItemPosition == 2) View.VISIBLE else View.GONE

        tvDelkaJednotky.visibility = if (spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        tvSirkaJednotky.visibility = if (spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        tvPrumerJednotky.visibility = if (spTvar.selectedItemPosition == 2) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        
        x(requireView())
    }

}