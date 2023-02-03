package com.regulus.dotaznik.dotaznik.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.regulus.dotaznik.R
import com.regulus.dotaznik.Stranka
import com.regulus.dotaznik.databinding.FragmentDetailObjektuBinding
import com.regulus.dotaznik.prihlaseni.PrihlaseniActivity
import com.regulus.dotaznik.saver

class DetailObjektuFragment : Fragment() {

    private fun save() {
        val stranky = requireContext().saver.get()

        stranky.detailObjektu.apply {
            ztrata = binding.etZtrata.editText!!.text.toString()
            potrebaVytapeni = binding.etPotrebaVytapeni.editText!!.text.toString()
            potrebaTv = binding.etPotrebaTv.editText!!.text.toString()
            plocha = binding.etPlocha.editText!!.text.toString()
            objem = binding.etObjem.editText!!.text.toString()
            naklady = binding.etNaklady.editText!!.text.toString()
            druhPaliva = binding.etDruh.editText!!.text.toString()
            spotreba = binding.etSpotreba.editText!!.text.toString()
            spotrebaJednotkyPos = binding.spSpotreba.selectedItemPosition
            spotrebaJednotky = binding.spSpotreba.selectedItem.toString()
            druhPaliva2 = binding.etDruh2.editText!!.text.toString()
            spotreba2 = binding.etSpotreba2.editText!!.text.toString()
            spotrebaJednotky2Pos = binding.spSpotreba2.selectedItemPosition
            spotrebaJednotky2 = binding.spSpotreba2.selectedItem.toString()
            poznamka = binding.etPoznamka2.editText!!.text.toString()
        }

        if (stranky.detailObjektu == Stranka.DetailObjektu()) return

        requireContext().saver.save(stranky)
    }

    private lateinit var binding: FragmentDetailObjektuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailObjektuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // adapter
        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.jednotky, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spSpotreba.adapter = adapter
        binding.spSpotreba2.adapter = adapter

        // znovu prihlasit
        binding.category8.setOnLongClickListener {
            val intent = Intent(activity, PrihlaseniActivity::class.java)
            startActivity(intent)

            return@setOnLongClickListener true
        }

        binding.etZtrata.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etPotrebaVytapeni.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etPotrebaTv.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etPlocha.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etObjem.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etNaklady.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etDruh.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etSpotreba.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etDruh2.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etSpotreba2.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etPoznamka2.editText!!.doOnTextChanged { _, _, _, _ -> save() }

        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                save()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                save()
            }
        }.also {
            binding.spSpotreba.onItemSelectedListener = it
            binding.spSpotreba2.onItemSelectedListener = it
        }

        // nacitani
        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {
            binding.etZtrata.editText!!.setText(stranky.detailObjektu.ztrata)
            binding.etPotrebaVytapeni.editText!!.setText(stranky.detailObjektu.potrebaVytapeni)
            binding.etPotrebaTv.editText!!.setText(stranky.detailObjektu.potrebaTv)
            binding.etPlocha.editText!!.setText(stranky.detailObjektu.plocha)
            binding.etObjem.editText!!.setText(stranky.detailObjektu.objem)
            binding.etNaklady.editText!!.setText(stranky.detailObjektu.naklady)
            binding.etDruh.editText!!.setText(stranky.detailObjektu.druhPaliva)
            binding.etSpotreba.editText!!.setText(stranky.detailObjektu.spotreba)
            binding.spSpotreba.setSelection(stranky.detailObjektu.spotrebaJednotkyPos)
            binding.etDruh2.editText!!.setText(stranky.detailObjektu.druhPaliva2)
            binding.etSpotreba2.editText!!.setText(stranky.detailObjektu.spotreba2)
            binding.spSpotreba2.setSelection(stranky.detailObjektu.spotrebaJednotky2Pos)
            binding.etPoznamka2.editText!!.setText(stranky.detailObjektu.poznamka)
        }
    }
}
