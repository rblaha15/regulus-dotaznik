package com.regulus.dotaznik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_bazen.*
import java.util.*


class BazenFragment : Fragment() {

    private var timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
        timer = Timer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_bazen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

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

        spDoba.adapter = adapter1
        spUmisteni.adapter = adapter2
        spDruhVody.adapter = adapter3
        spTvar.adapter = adapter4
        spZakryti.adapter = adapter5


        update()


        spTvar.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    update()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }


        val task = object : TimerTask() {
            override fun run() {

                val stranky = requireContext().saver.get()

                stranky.bazen.apply {
                    dobaPos = spDoba.selectedItemPosition
                    doba = spDoba.selectedItem.toString()
                    umisteniPos = spUmisteni.selectedItemPosition
                    umisteni = spUmisteni.selectedItem.toString()
                    druhVodyPos = spDruhVody.selectedItemPosition
                    druhVody = spDruhVody.selectedItem.toString()
                    tvarPos = spTvar.selectedItemPosition
                    tvar = spTvar.selectedItem.toString()
                    delka = etDelka.editText!!.text.toString()
                    sirka = etSirka.editText!!.text.toString()
                    prumer = etPrumer.editText!!.text.toString()
                    hloubka = etHloubka.editText!!.text.toString()
                    zakrytiPos = spZakryti.selectedItemPosition
                    zakryti = spZakryti.selectedItem.toString()
                    teplota = etTeplota.editText!!.text.toString()
                    poznamka = etPoznamka4.editText!!.text.toString()
                }

                if (stranky.bazen == Stranky.Bazen()) return

                requireContext().saver.save(stranky)
            }
        }

        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {

            spDoba.setSelection(stranky.bazen.dobaPos)
            spUmisteni.setSelection(stranky.bazen.umisteniPos)
            spDruhVody.setSelection(stranky.bazen.druhVodyPos)
            spTvar.setSelection(stranky.bazen.tvarPos)
            etDelka.editText!!.setText(stranky.bazen.delka)
            etSirka.editText!!.setText(stranky.bazen.sirka)
            etPrumer.editText!!.setText(stranky.bazen.prumer)
            etHloubka.editText!!.setText(stranky.bazen.hloubka)
            spZakryti.setSelection(stranky.bazen.zakrytiPos)
            etTeplota.editText!!.setText(stranky.bazen.teplota)
            etPoznamka4.editText!!.setText(stranky.bazen.poznamka)

        }

        timer.scheduleAtFixedRate(task, 0, 200)

    }

    private fun update() {

        etDelka.visibility = if (spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        etSirka.visibility = if (spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        etPrumer.visibility = if (spTvar.selectedItemPosition == 2) View.VISIBLE else View.GONE

    }

    override fun onResume() {
        super.onResume()
        
        init()
    }

}