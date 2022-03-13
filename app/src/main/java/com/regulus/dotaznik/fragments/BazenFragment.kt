package com.regulus.dotaznik.fragments

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.regulus.dotaznik.*
import com.regulus.dotaznik.databinding.FragmentBazenBinding
import java.util.*

class BazenFragment : Fragment() {

    private var timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
        timer = Timer()
    }

    private lateinit var binding: FragmentBazenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBazenBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun update() {

        binding.etDelka.visibility = if (binding.spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        binding.etSirka.visibility = if (binding.spTvar.selectedItemPosition != 2) View.VISIBLE else View.GONE
        binding.etPrumer.visibility = if (binding.spTvar.selectedItemPosition == 2) View.VISIBLE else View.GONE

        binding.svBazen.visibility =
            if (binding.cbBazen.isChecked) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter1 = ArrayAdapter.createFromResource(requireActivity(), R.array.dobaVyuzivani, android.R.layout.simple_spinner_item)
        val adapter2 = ArrayAdapter.createFromResource(requireActivity(), R.array.umisteni, android.R.layout.simple_spinner_item)
        val adapter3 = ArrayAdapter.createFromResource(requireActivity(), R.array.druhVody, android.R.layout.simple_spinner_item)
        val adapter4 = ArrayAdapter.createFromResource(requireActivity(), R.array.tvar, android.R.layout.simple_spinner_item)
        val adapter5 = ArrayAdapter.createFromResource(requireActivity(), R.array.zakryti, android.R.layout.simple_spinner_item)

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spDoba.adapter = adapter1
        binding.spUmisteni.adapter = adapter2
        binding.spDruhVody.adapter = adapter3
        binding.spTvar.adapter = adapter4
        binding.spZakryti.adapter = adapter5

        update()

        binding.spTvar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                update()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.cbBazen.setOnClickListener {
            update()
        }

        val task = object : TimerTask() {
            override fun run() {

                val stranky = requireContext().saver.get()

                stranky.bazen.apply {
                    chciBazen = binding.cbBazen.isChecked
                    dobaPos = binding.spDoba.selectedItemPosition
                    doba = binding.spDoba.selectedItem.toString()
                    umisteniPos = binding.spUmisteni.selectedItemPosition
                    umisteni = binding.spUmisteni.selectedItem.toString()
                    druhVodyPos = binding.spDruhVody.selectedItemPosition
                    druhVody = binding.spDruhVody.selectedItem.toString()
                    tvarPos = binding.spTvar.selectedItemPosition
                    tvar = binding.spTvar.selectedItem.toString()
                    delka = binding.etDelka.editText!!.text.toString()
                    sirka = binding.etSirka.editText!!.text.toString()
                    prumer = binding.etPrumer.editText!!.text.toString()
                    hloubka = binding.etHloubka.editText!!.text.toString()
                    zakrytiPos = binding.spZakryti.selectedItemPosition
                    zakryti = binding.spZakryti.selectedItem.toString()
                    teplota = binding.etTeplota.editText!!.text.toString()
                    poznamka = binding.etPoznamka4.editText!!.text.toString()
                }

                if (stranky.bazen == Stranky.Bazen()) return

                requireContext().saver.save(stranky)
            }
        }

        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {

            binding.cbBazen.isChecked = stranky.bazen.chciBazen
            binding.spDoba.setSelection(stranky.bazen.dobaPos)
            binding.spUmisteni.setSelection(stranky.bazen.umisteniPos)
            binding.spDruhVody.setSelection(stranky.bazen.druhVodyPos)
            binding.spTvar.setSelection(stranky.bazen.tvarPos)
            binding.etDelka.editText!!.setText(stranky.bazen.delka)
            binding.etSirka.editText!!.setText(stranky.bazen.sirka)
            binding.etPrumer.editText!!.setText(stranky.bazen.prumer)
            binding.etHloubka.editText!!.setText(stranky.bazen.hloubka)
            binding.spZakryti.setSelection(stranky.bazen.zakrytiPos)
            binding.etTeplota.editText!!.setText(stranky.bazen.teplota)
            binding.etPoznamka4.editText!!.setText(stranky.bazen.poznamka)

            update()
        }

        timer.scheduleAtFixedRate(task, 0, 200)
    }
}