package com.regulus.dotaznik

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_detail_objektu.*
import java.util.*


class DetailObjektuFragment : Fragment() {

    private var timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
        timer = Timer()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_objektu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {
        // adapter
        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.jednotky, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spSpotreba.adapter = adapter
        spSpotreba2.adapter = adapter

        // znovu prihlasit
        category8.setOnLongClickListener {
            val intent = Intent(activity, PrihlaseniActivity::class.java)
            startActivity(intent)

            return@setOnLongClickListener true
        }

        // saving
        val task = object : TimerTask() {
            override fun run() {

                val stranky = requireContext().saver.get()

                stranky.detailObjektu.apply {
                    ztrata = etZtrata.editText!!.text.toString()
                    potrebaVytapeni = etPotrebaVytapeni.editText!!.toString()
                    potrebaTv = etPotrebaTv.editText!!.text.toString()
                    plocha = etPlocha.editText!!.text.toString()
                    objem = etObjem.editText!!.text.toString()
                    naklady = etNaklady.editText!!.text.toString()
                    druhPaliva = etDruh.editText!!.text.toString()
                    spotreba = etSpotreba.editText!!.text.toString()
                    spotrebaJednotkyPos = spSpotreba.selectedItemPosition
                    spotrebaJednotky = spSpotreba.selectedItem.toString()
                    druhPaliva2 = etDruh2.editText!!.text.toString()
                    spotreba2 = etSpotreba2.editText!!.text.toString()
                    spotrebaJednotky2Pos = spSpotreba2.selectedItemPosition
                    spotrebaJednotky2 = spSpotreba2.selectedItem.toString()
                    poznamka = etPoznamka2.editText!!.text.toString()
                }

                if (stranky.detailObjektu == Stranky.DetailObjektu()) return

                requireContext().saver.save(stranky)
            }
        }


        // nacitani
        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {
            etZtrata.editText!!.setText(stranky.detailObjektu.ztrata)
            etPotrebaVytapeni.editText!!.setText(stranky.detailObjektu.potrebaVytapeni)
            etPotrebaTv.editText!!.setText(stranky.detailObjektu.potrebaTv)
            etPlocha.editText!!.setText(stranky.detailObjektu.plocha)
            etObjem.editText!!.setText(stranky.detailObjektu.objem)
            etNaklady.editText!!.setText(stranky.detailObjektu.naklady)
            etDruh.editText!!.setText(stranky.detailObjektu.druhPaliva)
            etSpotreba.editText!!.setText(stranky.detailObjektu.spotreba)
            spSpotreba.setSelection(stranky.detailObjektu.spotrebaJednotkyPos)
            etDruh2.editText!!.setText(stranky.detailObjektu.druhPaliva2)
            etSpotreba2.editText!!.setText(stranky.detailObjektu.spotreba2)
            spSpotreba2.setSelection(stranky.detailObjektu.spotrebaJednotky2Pos)
            etPoznamka2.editText!!.setText(stranky.detailObjektu.poznamka)
        }


        timer.scheduleAtFixedRate(task, 0, 200)
    }

    override fun onResume() {
        super.onResume()

        init()
    }


}