package com.regulus.dotaznik

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_kontakty.*
import java.util.*


class KontaktyFragment : Fragment() {


    private var timer = Timer()
    private var isFirst = true

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    /*override fun onPause() {
        super.onPause()
        timer.cancel()
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_kontakty, container, false)
        setHasOptionsMenu(true)

        x(view)

        return view
    }

    private fun x(view: View) {
        view.findViewById<Button>(R.id.btnVybratFirmu).setOnClickListener {
            val intent = Intent(requireActivity(), FirmyActivity::class.java)

            startActivity(intent)
        }

        Thread {

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




                    saver.save(stranky)
                }
            }


            timer.scheduleAtFixedRate(task, 0, 200)


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


        }.start()
    }

    override fun onResume() {
        super.onResume()

        if (isFirst) {
            isFirst = false
            return
        }

        x(requireView())

        (activity as MainActivity).recreateKontaktyFragment()

        x(requireView())

    }


}




