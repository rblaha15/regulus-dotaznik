package com.regulus.dotaznik.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.regulus.dotaznik.R
import com.regulus.dotaznik.Stranky
import com.regulus.dotaznik.activities.MainActivity
import com.regulus.dotaznik.databinding.FragmentPrislusenstviBinding
import com.regulus.dotaznik.saver


class PrislusenstviFragment : Fragment() {

    private fun save() {
        val stranky = requireContext().saver.get()

        stranky.prislusenstvi.apply {
            hadice = binding.cbHadice.isChecked
            hadiceTypPos = binding.spHadice.selectedItemPosition
            hadiceTyp = binding.spHadice.selectedItem.toString()
            topnyKabel = binding.cbTopnyKabel.isChecked
            topnyKabelTypPos = binding.spTopnyKabel.selectedItemPosition
            topnyKabelTyp = binding.spTopnyKabel.selectedItem.toString()
            drzakNaStenu = binding.cbDrzakNaStenu.isChecked
            drzakNaStenuTypPos = binding.spDrzakNaStenu.selectedItemPosition
            drzakNaStenuTyp = binding.spDrzakNaStenu.selectedItem.toString()
            pokojovaJednotka = binding.cbPokojovaJednotka.isChecked
            pokojovaJednotkaTypPos = binding.spPokojovaJednotka.selectedItemPosition
            pokojovaJednotkaTyp = binding.spPokojovaJednotka.selectedItem.toString()
            pokojoveCidlo = binding.cbPokojoveCidlo.isChecked
            pokojoveCidloTypPos = binding.spPokojoveCidlo.selectedItemPosition
            pokojoveCidloTyp = binding.spPokojoveCidlo.selectedItem.toString()
            poznamka = binding.etPoznamka.editText!!.text.toString()
        }

        if (stranky.prislusenstvi == Stranky.Prislusenstvi()) return

        requireContext().saver.save(stranky)
    }

    private lateinit var binding: FragmentPrislusenstviBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrislusenstviBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun update() {

        binding.spHadice.visibility = if (binding.cbHadice.isChecked) View.VISIBLE else View.GONE

        binding.spDrzakNaStenu.visibility = if (binding.cbDrzakNaStenu.isChecked) View.VISIBLE else View.GONE

        binding.spPokojovaJednotka.visibility = if (binding.cbPokojovaJednotka.isChecked) View.VISIBLE else View.GONE

        binding.spPokojoveCidlo.visibility = if (binding.cbPokojoveCidlo.isChecked) View.VISIBLE else View.GONE

        binding.spTopnyKabel.visibility = if (binding.cbTopnyKabel.isChecked) View.VISIBLE else View.GONE

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabOdeslat.setOnLongClickListener {

            (activity as MainActivity).debugMode = true
            (activity as MainActivity).odeslat()
            return@setOnLongClickListener true
        }

        binding.fabOdeslat.setOnClickListener {

            (activity as MainActivity).debugMode = false
            (activity as MainActivity).odeslat()
        }

        val adapter1 = ArrayAdapter.createFromResource(requireActivity(), R.array.topnyKabel, android.R.layout.simple_spinner_item)
        val adapter2 = ArrayAdapter.createFromResource(requireActivity(), R.array.hadice, android.R.layout.simple_spinner_item)
        val adapter3 = ArrayAdapter.createFromResource(requireActivity(), R.array.pokojovaJednotka, android.R.layout.simple_spinner_item)
        val adapter5 = ArrayAdapter.createFromResource(requireActivity(), R.array.pokojoveCidlo, android.R.layout.simple_spinner_item)
        val adapter4 = ArrayAdapter.createFromResource(requireActivity(), R.array.drzakNaTc, android.R.layout.simple_spinner_item)

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spTopnyKabel.adapter = adapter1
        binding.spHadice.adapter = adapter2
        binding.spPokojovaJednotka.adapter = adapter3
        binding.spPokojoveCidlo.adapter = adapter5
        binding.spDrzakNaStenu.adapter = adapter4

        update()

        binding.cbTopnyKabel.setOnClickListener { update(); save() }
        binding.cbHadice.setOnClickListener { update(); save() }
        binding.cbPokojovaJednotka.setOnClickListener { update(); save() }
        binding.cbPokojoveCidlo.setOnClickListener { update(); save() }
        binding.cbDrzakNaStenu.setOnClickListener { update(); save() }

        binding.tvTopnyKabel.setOnClickListener {
            binding.cbTopnyKabel.isChecked = !binding.cbTopnyKabel.isChecked
            update(); save()
        }
        binding.tvHadice.setOnClickListener {
            binding.cbHadice.isChecked = !binding.cbHadice.isChecked
            update(); save()
        }
        binding.tvPokojovaJednotka.setOnClickListener {
            binding.cbPokojovaJednotka.isChecked = !binding.cbPokojovaJednotka.isChecked
            update(); save()
        }
        binding.tvPokojoveCidlo.setOnClickListener {
            binding.cbPokojoveCidlo.isChecked = !binding.cbPokojoveCidlo.isChecked
            update(); save()
        }
        binding.tvDrzakNaStenu.setOnClickListener {
            binding.cbDrzakNaStenu.isChecked = !binding.cbDrzakNaStenu.isChecked
            update(); save()
        }


        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                save()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                save()
            }
        }.also {
            binding.spHadice.onItemSelectedListener = it
            binding.spTopnyKabel.onItemSelectedListener = it
            binding.spDrzakNaStenu.onItemSelectedListener = it
            binding.spPokojovaJednotka.onItemSelectedListener = it
            binding.spPokojoveCidlo.onItemSelectedListener = it
        }

        binding.etPoznamka.editText!!.doOnTextChanged { _, _, _, _ -> save() }

        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {

            stranky.prislusenstvi.apply {

                binding.cbHadice.isChecked = hadice
                binding.spHadice.setSelection(hadiceTypPos)
                binding.cbTopnyKabel.isChecked = topnyKabel
                binding.spTopnyKabel.setSelection(topnyKabelTypPos)
                binding.cbDrzakNaStenu.isChecked = drzakNaStenu
                binding.spDrzakNaStenu.setSelection(drzakNaStenuTypPos)
                binding.cbPokojovaJednotka.isChecked = pokojovaJednotka
                binding.spPokojovaJednotka.setSelection(pokojovaJednotkaTypPos)
                binding.cbPokojoveCidlo.isChecked = pokojoveCidlo
                binding.spPokojoveCidlo.setSelection(pokojoveCidloTypPos)
                binding.etPoznamka.editText!!.setText(poznamka)
            }
            update()
        }
    }
}
