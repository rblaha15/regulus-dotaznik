package com.regulus.dotaznik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_zdroje.*
import java.util.*


class ZdrojeFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_zdroje, container, false)

        x(view)

        return view
    }

    private fun update(view: View = requireView()) {


        val cbTopTopneTeleso: CheckBox = view.findViewById(R.id.cbTopTopneTeleso)
        val spTopTopneTeleso: Spinner = view.findViewById(R.id.spTopTopneTeleso)

        spTopTopneTeleso.visibility = if (cbTopTopneTeleso.isChecked) View.VISIBLE else View.GONE

        val cbTopElektrokotel: CheckBox = view.findViewById(R.id.cbTopElektrokotel)
        val spTopElektrokotel: Spinner = view.findViewById(R.id.spTopElektrokotel)

        spTopElektrokotel.visibility = if (cbTopElektrokotel.isChecked) View.VISIBLE else View.GONE

        val cbTopPlynKotel: CheckBox = view.findViewById(R.id.cbTopPlynKotel)
        val spTopPlynKotel: Spinner = view.findViewById(R.id.spTopPlynKotel)

        spTopPlynKotel.visibility = if (cbTopPlynKotel.isChecked) View.VISIBLE else View.GONE

        val cbTopKrb: CheckBox = view.findViewById(R.id.cbTopKrb)
        val spTopKrb: Spinner = view.findViewById(R.id.spTopKrb)

        spTopKrb.visibility = if (cbTopKrb.isChecked) View.VISIBLE else View.GONE



        val cbTvTopneTeleso: CheckBox = view.findViewById(R.id.cbTvTopneTeleso)
        val spTvTopneTeleso: Spinner = view.findViewById(R.id.spTvTopneTeleso)

        spTvTopneTeleso.visibility = if (cbTvTopneTeleso.isChecked) View.VISIBLE else View.GONE
    }

    private fun x(view: View) {

        val adapter1 = ArrayAdapter.createFromResource(requireActivity(), R.array.nove, android.R.layout.simple_spinner_item)
        val adapter2 = ArrayAdapter.createFromResource(requireActivity(), R.array.novy, android.R.layout.simple_spinner_item)

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.findViewById<Spinner>(R.id.spTopTopneTeleso).adapter = adapter1
        view.findViewById<Spinner>(R.id.spTopElektrokotel).adapter = adapter2
        view.findViewById<Spinner>(R.id.spTopPlynKotel).adapter = adapter2
        view.findViewById<Spinner>(R.id.spTopKrb).adapter = adapter2


        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.kamTeleso, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.findViewById<Spinner>(R.id.spTvTopneTeleso).adapter = adapter


        update(view)

        val cbTopTopneTeleso = view.findViewById<CheckBox>(R.id.cbTopTopneTeleso)
        val cbTopElektrokotel = view.findViewById<CheckBox>(R.id.cbTopElektrokotel)
        val cbTopPlynKotel = view.findViewById<CheckBox>(R.id.cbTopPlynKotel)
        val cbTopKrb = view.findViewById<CheckBox>(R.id.cbTopKrb)
        val cbTopJiny = view.findViewById<CheckBox>(R.id.cbTopJiny)

        val tvTopTopneTeleso = view.findViewById<TextView>(R.id.tvTopTopneTeleso)
        val tvTopElektrokotel = view.findViewById<TextView>(R.id.tvTopElektrokotel)
        val tvTopPlynKotel = view.findViewById<TextView>(R.id.tvTopPlynKotel)
        val tvTopKrb = view.findViewById<TextView>(R.id.tvTopKrb)
        val tvTopJiny = view.findViewById<TextView>(R.id.tvTopJiny)

        cbTopTopneTeleso.setOnClickListener { update() }
        cbTopElektrokotel.setOnClickListener { update() }
        cbTopPlynKotel.setOnClickListener { update() }
        cbTopKrb.setOnClickListener { update() }


        tvTopTopneTeleso.setOnClickListener { cbTopTopneTeleso.isChecked = !cbTopTopneTeleso.isChecked; update() }
        tvTopElektrokotel.setOnClickListener { cbTopElektrokotel.isChecked = !cbTopElektrokotel.isChecked; update() }
        tvTopPlynKotel.setOnClickListener { cbTopPlynKotel.isChecked = !cbTopPlynKotel.isChecked; update() }
        tvTopKrb.setOnClickListener { cbTopKrb.isChecked = !cbTopKrb.isChecked; update() }
        tvTopJiny.setOnClickListener { cbTopJiny.isChecked = !cbTopJiny.isChecked; update() }


        Thread {

            val task = object : TimerTask() {
                override fun run() {

                    val saver = Saver(requireActivity())
                    val stranky = saver.get()

                    stranky.zdrojeTop.apply {
                        topTopneTeleso = cbTopTopneTeleso.isChecked
                        topTopneTelesoTypPos = spTopTopneTeleso.selectedItemPosition
                        topTopneTelesoTyp = spTopTopneTeleso.selectedItem.toString()
                        topElektrokotel = cbTopElektrokotel.isChecked
                        topElektrokotelTypPos = spTopElektrokotel.selectedItemPosition
                        topElektrokotelTyp = spTopElektrokotel.selectedItem.toString()
                        topPlynKotel = cbTopPlynKotel.isChecked
                        topPlynKotelTypPos = spTopPlynKotel.selectedItemPosition
                        topPlynKotelTyp = spTopPlynKotel.selectedItem.toString()
                        topKrb = cbTopKrb.isChecked
                        topKrbTypPos = spTopKrb.selectedItemPosition
                        topKrbTyp = spTopKrb.selectedItem.toString()
                        topJiny = cbTopJiny.isChecked
                        topKtery = etTopJiny.text.toString()
                    }

                    stranky.zdrojeTv.apply {
                        tvTopneTeleso = cbTvTopneTeleso.isChecked
                        tvTopneTelesoTypPos = spTvTopneTeleso.selectedItemPosition
                        tvTopneTelesoTyp = spTvTopneTeleso.selectedItem.toString()
                        tvElektrokotel = cbTvElektrokotel.isChecked
                        tvPlynKotel = cbTvPlynKotel.isChecked
                        tvKrb = cbTvKrb.isChecked
                        tvJiny = cbTvJiny.isChecked
                        tvKtery = etTvJiny.text.toString()
                        poznamka = etPoznamka6.text.toString()
                    }


                    saver.save(stranky)
                }
            }


            timer.scheduleAtFixedRate(task, 0, 200)


            val saver = Saver(requireActivity())
            val stranky = saver.get()


            requireActivity().runOnUiThread {

                cbTopTopneTeleso.isChecked = stranky.zdrojeTop.topTopneTeleso
                spTopTopneTeleso.setSelection(stranky.zdrojeTop.topTopneTelesoTypPos)
                cbTopElektrokotel.isChecked = stranky.zdrojeTop.topElektrokotel
                spTopElektrokotel.setSelection(stranky.zdrojeTop.topElektrokotelTypPos)
                cbTopPlynKotel.isChecked = stranky.zdrojeTop.topPlynKotel
                spTopElektrokotel.setSelection(stranky.zdrojeTop.topPlynKotelTypPos)
                cbTopKrb.isChecked = stranky.zdrojeTop.topKrb
                spTopKrb.setSelection(stranky.zdrojeTop.topKrbTypPos)
                cbTopJiny.isChecked = stranky.zdrojeTop.topJiny
                etTopJiny.setText(stranky.zdrojeTop.topKtery)

                cbTvTopneTeleso.isChecked = stranky.zdrojeTv.tvTopneTeleso
                spTvTopneTeleso.setSelection(stranky.zdrojeTv.tvTopneTelesoTypPos)
                cbTvElektrokotel.isChecked = stranky.zdrojeTv.tvElektrokotel
                cbTvPlynKotel.isChecked = stranky.zdrojeTv.tvPlynKotel
                cbTvKrb.isChecked = stranky.zdrojeTv.tvKrb
                cbTvJiny.isChecked = stranky.zdrojeTv.tvJiny
                etTvJiny.setText(stranky.zdrojeTv.tvKtery)
                etPoznamka6.setText(stranky.zdrojeTv.poznamka)

                update(view)

            }



        }.start()




        val cbTvTopneTeleso = view.findViewById<CheckBox>(R.id.cbTvTopneTeleso)
        val cbTvElektrokotel = view.findViewById<CheckBox>(R.id.cbTvElektrokotel)
        val cbTvPlynKotel = view.findViewById<CheckBox>(R.id.cbTvPlynKotel)
        val cbTvKrb = view.findViewById<CheckBox>(R.id.cbTvKrb)
        val cbTvJiny = view.findViewById<CheckBox>(R.id.cbTvJiny)

        val tvTvTopneTeleso = view.findViewById<TextView>(R.id.tvTvTopneTeleso)
        val tvTvElektrokotel = view.findViewById<TextView>(R.id.tvTvElektrokotel)
        val tvTvPlynKotel = view.findViewById<TextView>(R.id.tvTvPlynKotel)
        val tvTvKrb = view.findViewById<TextView>(R.id.tvTvKrb)
        val tvTvJiny = view.findViewById<TextView>(R.id.tvTvJiny)

        cbTvTopneTeleso.setOnClickListener { update() }


        tvTvTopneTeleso.setOnClickListener { cbTvTopneTeleso.isChecked = !cbTvTopneTeleso.isChecked; update() }
        tvTvElektrokotel.setOnClickListener { cbTvElektrokotel.isChecked = !cbTvElektrokotel.isChecked; update() }
        tvTvPlynKotel.setOnClickListener { cbTvPlynKotel.isChecked = !cbTvPlynKotel.isChecked; update() }
        tvTvKrb.setOnClickListener { cbTvKrb.isChecked = !cbTvKrb.isChecked; update() }
        tvTvJiny.setOnClickListener { cbTvJiny.isChecked = !cbTvJiny.isChecked; update() }



    }

    override fun onResume() {
        super.onResume()

        x(requireView())
    }
}