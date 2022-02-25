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

        btnVybratFirmu.setOnClickListener {
            val intent = Intent(requireActivity(), FirmyActivity::class.java)

            startActivity(intent)
        }

        val task = object : TimerTask() {
            override fun run() {

                val stranky = requireContext().saver.get()

                val firmy = resources.getStringArray(R.array.ica)

                stranky.kontakty.apply {
                    prijmeni = etPrijmeni.editText!!.text.toString()
                    jmeno = etJmeno.editText!!.text.toString()
                    ulice = etUlice.editText!!.text.toString()
                    mesto = etMesto.editText!!.text.toString()
                    psc = etPSC.editText!!.text.toString()
                    telefon = etTelefon.editText!!.text.toString()
                    email = etEmail.editText!!.text.toString()
                    ico = etIco.editText!!.text.toString()
                    poznamka = etPoznamka.editText!!.text.toString()

                    firma = firmy.firstOrNull { it.split(" – ").last() == ico }?.split(" – ")?.first()
                        ?: ""



                    activity?.runOnUiThread {
                        btnVybratFirmu?.text =
                            if (firma.isEmpty())
                                getString(R.string.kontakty_vybrat_firmu)
                            else
                                firma
                    }
                }


                if (stranky.kontakty == Stranky.Kontakty()) return


                requireContext().saver.save(stranky)
            }
        }


        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {

            etPrijmeni.editText!!.setText(stranky.kontakty.prijmeni)
            etJmeno.editText!!.setText(stranky.kontakty.jmeno)
            etUlice.editText!!.setText(stranky.kontakty.ulice)
            etMesto.editText!!.setText(stranky.kontakty.mesto)
            etPSC.editText!!.setText(stranky.kontakty.psc)
            etTelefon.editText!!.setText(stranky.kontakty.telefon)
            etEmail.editText!!.setText(stranky.kontakty.email)
            etIco.editText!!.setText(stranky.kontakty.ico)
            btnVybratFirmu.text =
                if (stranky.kontakty.firma.isEmpty())
                    "Vyberat firmu"
                else
                    stranky.kontakty.firma
            etPoznamka.editText!!.setText(stranky.kontakty.poznamka)

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

    }


}




