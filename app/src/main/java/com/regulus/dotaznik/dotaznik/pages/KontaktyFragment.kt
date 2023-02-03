package com.regulus.dotaznik.dotaznik.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.regulus.dotaznik.R
import com.regulus.dotaznik.Stranka
import com.regulus.dotaznik.databinding.FragmentKontaktyBinding
import com.regulus.dotaznik.prefsPrihlaseni
import com.regulus.dotaznik.saver
import com.regulus.dotaznik.vybiratorFirem.FirmyActivity


class KontaktyFragment : Fragment() {

    fun save() {

        val stranky = requireContext().saver.get()

        val firmy = context.prefsPrihlaseni.getString("firmy", "")!!.split("\n")

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
        if (stranky.kontakty == Stranka.Kontakty()) return

        requireContext().saver.save(stranky)
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

    private fun init() {

        binding.etPrijmeni.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etJmeno.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etUlice.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etMesto.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etPSC.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etTelefon.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etEmail.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etIco.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etPoznamka.editText!!.doOnTextChanged { _, _, _, _ -> save() }

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

            binding.btnVybratFirmu.text = stranky.kontakty.firma.ifEmpty { context?.getString(R.string.kontakty_vybrat_firmu) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            save()

            init()
        }

        binding.btnVybratFirmu.setOnClickListener {
            val intent = Intent(requireActivity(), FirmyActivity::class.java)

            activityResultLauncher.launch(intent)
        }

        init()
    }
}
