package com.regulus.dotaznik.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.regulus.dotaznik.R
import com.regulus.dotaznik.Stranky
import com.regulus.dotaznik.activities.FirmyActivity
import com.regulus.dotaznik.databinding.FragmentKontaktyBinding
import com.regulus.dotaznik.saver
import java.util.*


class KontaktyFragment : Fragment() {

    private var timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
        timer = Timer()
    }

    private lateinit var binding: FragmentKontaktyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        binding = FragmentKontaktyBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun init() {
        val task = object : TimerTask() {
            override fun run() {

                val stranky = requireContext().saver.get()

                val firmy = resources.getStringArray(R.array.ica)

                stranky.kontakty.apply {
                    prijmeni = binding.etPrijmeni.editText!!.text.toString()
                    jmeno = binding.etJmeno.editText!!.text.toString()
                    ulice = binding.etUlice.editText!!.text.toString()
                    mesto = binding.etMesto.editText!!.text.toString()
                    psc = binding.etPSC.editText!!.text.toString()
                    telefon = binding.etTelefon.editText!!.text.toString()
                    email = binding.etEmail.editText!!.text.toString()
                    ico = binding.etIco.editText!!.text.toString()
                    poznamka = binding.etPoznamka.editText!!.text.toString()

                    firma = firmy.firstOrNull { it.split(" – ").last() == ico }?.split(" – ")?.first() ?: ""

                    activity?.runOnUiThread {
                        binding.btnVybratFirmu.text = firma.ifEmpty { context?.getString(R.string.kontakty_vybrat_firmu) }
                    }
                }


                if (stranky.kontakty == Stranky.Kontakty()) return


                requireContext().saver.save(stranky)
            }
        }

        timer.scheduleAtFixedRate(task, 0, 200)

        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {

            binding.etPrijmeni.editText!!.setText(stranky.kontakty.prijmeni)
            binding.etJmeno.editText!!.setText(stranky.kontakty.jmeno)
            binding.etUlice.editText!!.setText(stranky.kontakty.ulice)
            binding.etMesto.editText!!.setText(stranky.kontakty.mesto)
            binding.etPSC.editText!!.setText(stranky.kontakty.psc)
            binding.etTelefon.editText!!.setText(stranky.kontakty.telefon)
            binding.etEmail.editText!!.setText(stranky.kontakty.email)
            binding.etIco.editText!!.setText(stranky.kontakty.ico)
            binding.etPoznamka.editText!!.setText(stranky.kontakty.poznamka)

            binding.btnVybratFirmu.text = stranky.kontakty.firma.ifEmpty { "Vyberat firmu" }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            init()
        }

        binding.btnVybratFirmu.setOnClickListener {
            val intent = Intent(requireActivity(), FirmyActivity::class.java)

            activityResultLauncher.launch(intent)
        }

        init()
    }
}
