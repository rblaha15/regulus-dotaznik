package com.regulus.dotaznik

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_kontakty.*
import java.util.*


class KontaktyFragment : Fragment() {

    private var isFirst = true

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
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_kontakty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        btnVybratFirmu.setOnClickListener {
            val intent = Intent(requireActivity(), FirmyActivity::class.java)

            startActivity(intent)
        }


        val task = object : TimerTask() {
            override fun run() {

                val saver = Saver(requireActivity())
                val stranky = saver.get()

                val firmy = resources.getStringArray(R.array.ica).toList()

                stranky.kontakty.apply {
                    prijmeni = etPrijmeni.text.toString()
                    jmeno = etJmeno.text.toString()
                    ulice = etUlice.text.toString()
                    mesto = etMesto.text.toString()
                    psc = etPSC.text.toString()
                    telefon = etTelefon.text.toString()
                    email = etEmail.text.toString()
                    ico = etIco.text.toString()
                    poznamka = etPoznamka.text.toString()

                    firma =
                        if (ico in firmy.map { it.split(" - ")[1] })
                            firmy.filter { it.contains(ico) }[0].split(" - ")[0]
                        else
                            ""


                    activity?.runOnUiThread {
                        btnVybratFirmu?.text =
                            if (firma.isEmpty())
                                getString(R.string.kontakty_vybrat_firmu)
                            else
                                firma
                    }
                }


                if (stranky.kontakty == Stranky.Kontakty()) return


                saver.save(stranky)
            }
        }




        val saver = Saver(requireActivity())
        val stranky = saver.get()

        requireActivity().runOnUiThread {

            etPrijmeni.setText(stranky.kontakty.prijmeni)
            etJmeno.setText(stranky.kontakty.jmeno)
            etUlice.setText(stranky.kontakty.ulice)
            etMesto.setText(stranky.kontakty.mesto)
            etPSC.setText(stranky.kontakty.psc)
            etTelefon.setText(stranky.kontakty.telefon)
            etEmail.setText(stranky.kontakty.email)
            etIco.setText(stranky.kontakty.ico)
            btnVybratFirmu.text =
                if (stranky.kontakty.firma.isEmpty())
                    "Vyberat firmu"
                else
                    stranky.kontakty.firma
            etPoznamka.setText(stranky.kontakty.poznamka)

        }

        timer.scheduleAtFixedRate(task, 0, 200)
    }

    override fun onResume() {
        super.onResume()

        if (isFirst) {
            isFirst = false
            return
        }

        (activity as MainActivity).recreateKontaktyFragment()

        //init()

    }


}




