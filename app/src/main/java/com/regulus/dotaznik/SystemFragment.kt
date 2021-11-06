package com.regulus.dotaznik

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_system.*
import java.util.*


class SystemFragment : Fragment() {

    private lateinit var adapter2 : ArrayAdapter<CharSequence>
    private lateinit var adapter2a: ArrayAdapter<CharSequence>
    private lateinit var adapter5 : ArrayAdapter<CharSequence>
    private lateinit var adapter5a: ArrayAdapter<CharSequence>
    private lateinit var adapter5b: ArrayAdapter<CharSequence>

    private val timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_system, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter1  = ArrayAdapter.createFromResource(requireActivity(), R.array.tcTyp, android.R.layout.simple_spinner_item)
            adapter2  = ArrayAdapter.createFromResource(requireActivity(), R.array.tcModel, android.R.layout.simple_spinner_item)
            adapter2a = ArrayAdapter.createFromResource(requireActivity(), R.array.tcModelA, android.R.layout.simple_spinner_item)
        val adapter3  = ArrayAdapter.createFromResource(requireActivity(), R.array.jednotka, android.R.layout.simple_spinner_item)
        val adapter4  = ArrayAdapter.createFromResource(requireActivity(), R.array.nadrzTyp1, android.R.layout.simple_spinner_item)
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


        spTcTyp.adapter = adapter1
        spTcModel.adapter = adapter2
        spJednotka.adapter = adapter3
        spNadrzTyp1.adapter = adapter4
        spNadrzTyp2.adapter = adapter5
        spZasobnikTyp.adapter = adapter6
        spOtopnySystem.adapter = adapter7

        var poprve1 = 0
        var poprve2 = 0

        val onClick = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val saver = Saver(requireActivity())
                val stranky = saver.get()


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


                saver.save(stranky)

                update()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }




        spTcTyp.onItemSelectedListener = onClick
        spNadrzTyp1.onItemSelectedListener = onClick
        spZasobnikTyp.onItemSelectedListener = onClick
        cbBazen.setOnClickListener {
            update()
        }


        update(view)

        val task = object : TimerTask() {
            override fun run() {

                val saver = Saver(requireActivity())
                val stranky = saver.get()

                stranky.system.apply {
                    tcTypPos = spTcTyp.selectedItemPosition
                    tcTyp = spTcTyp.selectedItem.toString()
                    tcModelPos = spTcModel.selectedItemPosition
                    tcModel = spTcModel.selectedItem.toString()
                    jednotkaTypPos = spJednotka.selectedItemPosition
                    jednotkaTyp = spJednotka.selectedItem.toString()
                    nadrzTypPos = spNadrzTyp1.selectedItemPosition
                    nadrzTyp = spNadrzTyp1.selectedItem.toString()
                    nadrzTyp2Pos = spNadrzTyp2.selectedItemPosition
                    nadrzTyp2 = spNadrzTyp2.selectedItem.toString()
                    nadrzObjem = etNadrzObjem.text.toString()
                    zasobnikTypPos = spZasobnikTyp.selectedItemPosition
                    zasobnikTyp = spZasobnikTyp.selectedItem.toString()
                    zasobnikObjem = etZasobnikObjem.text.toString()
                    osPos = spOtopnySystem.selectedItemPosition
                    os = spOtopnySystem.selectedItem.toString()
                    cirkulace = cbCirkulace.isChecked
                    chciBazen = cbBazen.isChecked
                    poznamka = etPoznamka3.text.toString()
                }


                saver.save(stranky)


            }
        }


        timer.scheduleAtFixedRate(task, 0, 200)


        val saver = Saver(requireActivity())
        val stranky = saver.get()

        //Log.d("bazen", stranky.system.chciBazen.toString())


        requireActivity().runOnUiThread {

            spTcTyp.setSelection(stranky.system.tcTypPos)
            spTcModel.setSelection(stranky.system.tcModelPos)
            spJednotka.setSelection(stranky.system.jednotkaTypPos)
            spNadrzTyp1.setSelection(stranky.system.nadrzTypPos)
            spNadrzTyp2.setSelection(stranky.system.nadrzTyp2Pos)
            etNadrzObjem.setText(stranky.system.nadrzObjem)
            spZasobnikTyp.setSelection(stranky.system.zasobnikTypPos)
            etZasobnikObjem.setText(stranky.system.zasobnikObjem)
            spOtopnySystem.setSelection(stranky.system.osPos)
            cbCirkulace.isChecked = stranky.system.cirkulace
            cbBazen.isChecked = stranky.system.chciBazen
            etPoznamka3.setText(stranky.system.poznamka)

        }

    }


    private fun update(view: View = requireView()) {


        val spNadrzTyp1: Spinner = view.findViewById(R.id.spNadrzTyp1)
        val spNadrzTyp2: Spinner = view.findViewById(R.id.spNadrzTyp2)
        val etNadrzObjem: EditText = view.findViewById(R.id.etNadrzObjem)
        val tvNadrzObjem: TextView = view.findViewById(R.id.tvNadrzObjem)

        spNadrzTyp2.visibility = if (spNadrzTyp1.selectedItemPosition != 0) View.VISIBLE else View.GONE
        etNadrzObjem.visibility = if (spNadrzTyp1.selectedItemPosition != 0) View.VISIBLE else View.GONE
        tvNadrzObjem.visibility = if (spNadrzTyp1.selectedItemPosition != 0) View.VISIBLE else View.GONE


        val spZasobnikTyp: Spinner = view.findViewById(R.id.spZasobnikTyp)
        val etZasobnikObjem: EditText = view.findViewById(R.id.etZasobnikObjem)
        val tvZasobnikObjem: TextView = view.findViewById(R.id.tvZasobnikObjem)

        etZasobnikObjem.visibility = if (spZasobnikTyp.selectedItemPosition != 0) View.VISIBLE else View.GONE
        tvZasobnikObjem.visibility = if (spZasobnikTyp.selectedItemPosition != 0) View.VISIBLE else View.GONE

        val spTcTyp: Spinner = view.findViewById(R.id.spTcTyp)
        val spTcModel: Spinner = view.findViewById(R.id.spTcModel)

        spTcModel.adapter = if (spTcTyp.selectedItemPosition != 0) adapter2a else adapter2


        spNadrzTyp2.adapter = when (spNadrzTyp1.selectedItemPosition) {
            1 -> adapter5
            2 -> adapter5a
            else -> adapter5b
        }



        val saver = Saver(requireActivity())
        val stranky = saver.get()

        spTcModel.setSelection(stranky.system.tcModelPos)
        spNadrzTyp2.setSelection(stranky.system.nadrzTyp2Pos)

        return

    }


    override fun onResume() {
        super.onResume()

        onViewCreated(requireView(), null)
    }

}