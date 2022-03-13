package com.regulus.dotaznik.fragments

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.regulus.dotaznik.*
import com.regulus.dotaznik.databinding.FragmentSystemBinding
import java.util.*


class SystemFragment : Fragment() {

    private lateinit var adapter2 : ArrayAdapter<CharSequence>
    private lateinit var adapter2a: ArrayAdapter<CharSequence>
    private lateinit var adapter5 : ArrayAdapter<CharSequence>
    private lateinit var adapter5a: ArrayAdapter<CharSequence>
    private lateinit var adapter5b: ArrayAdapter<CharSequence>

    private var timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
        timer = Timer()
    }

    private lateinit var binding: FragmentSystemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSystemBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun update() {
        binding.spNadrzTyp2.visibility = if (binding.spNadrzTyp1.selectedItemPosition != 0) View.VISIBLE else View.GONE
        binding.etNadrzObjem.visibility = if (binding.spNadrzTyp1.selectedItemPosition != 0) View.VISIBLE else View.GONE

        binding.etZasobnikObjem.visibility = if (binding.spZasobnikTyp.selectedItemPosition != 0) View.VISIBLE else View.GONE

        binding.spTcModel.adapter = if (binding.spTcTyp.selectedItemPosition != 0) adapter2a else adapter2


        binding.spNadrzTyp2.adapter = when (binding.spNadrzTyp1.selectedItemPosition) {
            1 -> adapter5
            2 -> adapter5a
            else -> adapter5b
        }

        val stranky = requireContext().saver.get()

        binding.spTcModel.setSelection(stranky.system.tcModelPos)
        binding.spNadrzTyp2.setSelection(stranky.system.nadrzTyp2Pos)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter1  = ArrayAdapter.createFromResource(requireActivity(), R.array.tcTyp, android.R.layout.simple_spinner_item)
        adapter2  = ArrayAdapter.createFromResource(requireActivity(), R.array.tcModel, android.R.layout.simple_spinner_item)
        adapter2a = ArrayAdapter.createFromResource(requireActivity(), R.array.tcModelA, android.R.layout.simple_spinner_item)
        val adapter3  = ArrayAdapter.createFromResource(requireActivity(), R.array.jednotka, android.R.layout.simple_spinner_item)
        val adapter4  = ArrayAdapter.createFromResource(requireActivity(),
            R.array.nadrzTyp1, android.R.layout.simple_spinner_item)
        adapter5  = ArrayAdapter.createFromResource(requireActivity(), R.array.nadrzTyp2, android.R.layout.simple_spinner_item)
        adapter5a = ArrayAdapter.createFromResource(requireActivity(), R.array.nadrzTyp2A, android.R.layout.simple_spinner_item)
        adapter5b = ArrayAdapter.createFromResource(requireActivity(), R.array.nadrzTyp2B, android.R.layout.simple_spinner_item)
        val adapter6  = ArrayAdapter.createFromResource(requireActivity(), R.array.zasobnik, android.R.layout.simple_spinner_item)
        val adapter7  = ArrayAdapter.createFromResource(requireActivity(), R.array.os, android.R.layout.simple_spinner_item)

        adapter1 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter3 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter4 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter5 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter5a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter5b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter6 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter7 .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spTcTyp.adapter = adapter1
        binding.spTcModel.adapter = adapter2
        binding.spJednotka.adapter = adapter3
        binding.spNadrzTyp1.adapter = adapter4
        binding.spNadrzTyp2.adapter = adapter5
        binding.spZasobnikTyp.adapter = adapter6
        binding.spOtopnySystem.adapter = adapter7

        var poprve1 = 0
        var poprve2 = 0

        val onClick = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val stranky = requireContext().saver.get()

                when (parent!!.id) {
                    R.id.spTcTyp -> if ((stranky.system.tcModelPos != position) and (poprve1 == 1)) {
                        stranky.system.tcModelPos = 0
                        stranky.system.tcModel = ""
                    } else poprve1 = 1
                    R.id.spNadrzTyp1 -> if ((stranky.system.nadrzTyp2Pos != position) and (poprve2 == 1)) {
                        stranky.system.nadrzTyp2Pos = 0
                        stranky.system.nadrzTyp2 = ""
                    } else poprve2 = 1
                }

                requireContext().saver.save(stranky)

                update()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spTcTyp.onItemSelectedListener = onClick
        binding.spNadrzTyp1.onItemSelectedListener = onClick
        binding.spZasobnikTyp.onItemSelectedListener = onClick

        update()

        val task = object : TimerTask() {
            override fun run() {

                val stranky = requireContext().saver.get()

                stranky.system.apply {
                    tcTypPos = binding.spTcTyp.selectedItemPosition
                    tcTyp = binding.spTcTyp.selectedItem.toString()
                    tcModelPos = binding.spTcModel.selectedItemPosition
                    tcModel = binding.spTcModel.selectedItem.toString()
                    jednotkaTypPos = binding.spJednotka.selectedItemPosition
                    jednotkaTyp = binding.spJednotka.selectedItem.toString()
                    nadrzTypPos = binding.spNadrzTyp1.selectedItemPosition
                    nadrzTyp = binding.spNadrzTyp1.selectedItem.toString()
                    nadrzTyp2Pos = binding.spNadrzTyp2.selectedItemPosition
                    nadrzTyp2 = binding.spNadrzTyp2.selectedItem.toString()
                    nadrzObjem = binding.etNadrzObjem.editText!!.text.toString()
                    zasobnikTypPos = binding.spZasobnikTyp.selectedItemPosition
                    zasobnikTyp = binding.spZasobnikTyp.selectedItem.toString()
                    zasobnikObjem = binding.etZasobnikObjem.editText!!.text.toString()
                    osPos = binding.spOtopnySystem.selectedItemPosition
                    os = binding.spOtopnySystem.selectedItem.toString()
                    cirkulace = binding.cbCirkulace.isChecked
                    poznamka = binding.etPoznamka3.editText!!.text.toString()
                }

                if (stranky.system == Stranky.System_()) return

                requireContext().saver.save(stranky)

            }
        }

        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {

            binding.spTcTyp.setSelection(stranky.system.tcTypPos)
            binding.spTcModel.setSelection(stranky.system.tcModelPos)
            binding.spJednotka.setSelection(stranky.system.jednotkaTypPos)
            binding.spNadrzTyp1.setSelection(stranky.system.nadrzTypPos)
            binding.spNadrzTyp2.setSelection(stranky.system.nadrzTyp2Pos)
            binding.etNadrzObjem.editText!!.setText(stranky.system.nadrzObjem)
            binding.spZasobnikTyp.setSelection(stranky.system.zasobnikTypPos)
            binding.etZasobnikObjem.editText!!.setText(stranky.system.zasobnikObjem)
            binding.spOtopnySystem.setSelection(stranky.system.osPos)
            binding.cbCirkulace.isChecked = stranky.system.cirkulace
            binding.etPoznamka3.editText!!.setText(stranky.system.poznamka)

        }
        timer.scheduleAtFixedRate(task, 0, 200)
    }

}