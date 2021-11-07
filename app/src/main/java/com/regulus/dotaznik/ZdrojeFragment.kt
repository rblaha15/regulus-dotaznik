package com.regulus.dotaznik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_zdroje.*
import java.util.*


class ZdrojeFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_zdroje, container, false)
    }

    private fun update() {



        spTopTopneTeleso.visibility = if (cbTopTopneTeleso.isChecked) View.VISIBLE else View.GONE

        spTopElektrokotel.visibility = if (cbTopElektrokotel.isChecked) View.VISIBLE else View.GONE

        spTopPlynKotel.visibility = if (cbTopPlynKotel.isChecked) View.VISIBLE else View.GONE

        spTopKrb.visibility = if (cbTopKrb.isChecked) View.VISIBLE else View.GONE

        spTvTopneTeleso.visibility = if (cbTvTopneTeleso.isChecked) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        val adapter1 = ArrayAdapter.createFromResource(requireActivity(), R.array.nove, android.R.layout.simple_spinner_item)
        val adapter2 = ArrayAdapter.createFromResource(requireActivity(), R.array.novy, android.R.layout.simple_spinner_item)

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spTopTopneTeleso.adapter = adapter1
        spTopElektrokotel.adapter = adapter2
        spTopPlynKotel.adapter = adapter2
        spTopKrb.adapter = adapter2


        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.kamTeleso, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spTvTopneTeleso.adapter = adapter

        update()

        cbTopTopneTeleso.setOnClickListener { update() }
        cbTopElektrokotel.setOnClickListener { update() }
        cbTopPlynKotel.setOnClickListener { update() }
        cbTopKrb.setOnClickListener { update() }


        tvTopTopneTeleso.setOnClickListener { cbTopTopneTeleso.isChecked = !cbTopTopneTeleso.isChecked; update() }
        tvTopElektrokotel.setOnClickListener { cbTopElektrokotel.isChecked = !cbTopElektrokotel.isChecked; update() }
        tvTopPlynKotel.setOnClickListener { cbTopPlynKotel.isChecked = !cbTopPlynKotel.isChecked; update() }
        tvTopKrb.setOnClickListener { cbTopKrb.isChecked = !cbTopKrb.isChecked; update() }
        tvTopJiny.setOnClickListener { cbTopJiny.isChecked = !cbTopJiny.isChecked; update() }


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

                if (stranky.zdrojeTop == Stranky.ZdrojeTop()) return
                if (stranky.zdrojeTv == Stranky.ZdrojeTv()) return

                saver.save(stranky)
            }
        }




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

            update()

        }

        timer.scheduleAtFixedRate(task, 0, 200)

        cbTvTopneTeleso.setOnClickListener { update() }


        tvTvTopneTeleso.setOnClickListener { cbTvTopneTeleso.isChecked = !cbTvTopneTeleso.isChecked; update() }
        tvTvElektrokotel.setOnClickListener { cbTvElektrokotel.isChecked = !cbTvElektrokotel.isChecked; update() }
        tvTvPlynKotel.setOnClickListener { cbTvPlynKotel.isChecked = !cbTvPlynKotel.isChecked; update() }
        tvTvKrb.setOnClickListener { cbTvKrb.isChecked = !cbTvKrb.isChecked; update() }
        tvTvJiny.setOnClickListener { cbTvJiny.isChecked = !cbTvJiny.isChecked; update() }



    }

    override fun onResume() {
        super.onResume()

        init()
    }
}