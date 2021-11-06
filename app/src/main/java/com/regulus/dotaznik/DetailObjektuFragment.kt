package com.regulus.dotaznik

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_detail_objektu.*
import java.util.*


class DetailObjektuFragment : Fragment() {

    private val timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_objektu, container, false)

        x(view)

        return view
    }

    private fun x(view: View) {

        val adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.jednotky,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.findViewById<Spinner>(R.id.spSpotreba).adapter = adapter
        view.findViewById<Spinner>(R.id.spSpotreba2).adapter = adapter

        view.findViewById<TextView>(R.id.tvPotrebaVytapeniJednotky).setOnLongClickListener {
            val intent = Intent(activity, PrihlaseniActivity::class.java)
            startActivity(intent)



            return@setOnLongClickListener true
        }

        Thread {

            val task = object : TimerTask() {
                override fun run() {

                    val saver = Saver(requireActivity())
                    val stranky = saver.get()

                    stranky.detailObjektu.apply {
                        ztrata = etZtrata.text.toString()
                        potrebaVytapeni = etPotrebaVytapeni.text.toString()
                        potrebaTv = etPotrebaTv.text.toString()
                        plocha = etPlocha.text.toString()
                        objem = etObjem.text.toString()
                        naklady = etNaklady.text.toString()
                        druhPaliva = etDruh.text.toString()
                        spotreba = etSpotreba.text.toString()
                        spotrebaJednotkyPos = spSpotreba.selectedItemPosition
                        spotrebaJednotky = spSpotreba.selectedItem.toString()
                        druhPaliva2 = etDruh2.text.toString()
                        spotreba2 = etSpotreba2.text.toString()
                        spotrebaJednotky2Pos = spSpotreba2.selectedItemPosition
                        spotrebaJednotky2 = spSpotreba2.selectedItem.toString()
                        poznamka = etPoznamka2.text.toString()
                    }


                    saver.save(stranky)
                }
            }


            timer.scheduleAtFixedRate(task, 0, 200)


            val saver = Saver(requireActivity())
            val stranky = saver.get()

            requireActivity().runOnUiThread {
                etZtrata.setText(stranky.detailObjektu.ztrata)
                etPotrebaVytapeni.setText(stranky.detailObjektu.potrebaVytapeni)
                etPotrebaTv.setText(stranky.detailObjektu.potrebaTv)
                etPlocha.setText(stranky.detailObjektu.plocha)
                etObjem.setText(stranky.detailObjektu.objem)
                etNaklady.setText(stranky.detailObjektu.naklady)
                etDruh.setText(stranky.detailObjektu.druhPaliva)
                etSpotreba.setText(stranky.detailObjektu.spotreba)
                spSpotreba.setSelection(stranky.detailObjektu.spotrebaJednotkyPos)
                etDruh2.setText(stranky.detailObjektu.druhPaliva2)
                etSpotreba2.setText(stranky.detailObjektu.spotreba2)
                spSpotreba2.setSelection(stranky.detailObjektu.spotrebaJednotky2Pos)
                etPoznamka2.setText(stranky.detailObjektu.poznamka)
            }


        }.start()
    }

    override fun onResume() {
        super.onResume()

        x(requireView())
    }


}