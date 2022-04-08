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
import com.regulus.dotaznik.databinding.FragmentZdrojeBinding
import com.regulus.dotaznik.saver


class ZdrojeFragment : Fragment() {

    private fun save() {
        val stranky = requireContext().saver.get()

        stranky.zdrojeTop.apply {
            topneTeleso = binding.cbTopTopneTeleso.isChecked
            topneTelesoTypPos = binding.spTopTopneTeleso.selectedItemPosition
            topneTelesoTyp = binding.spTopTopneTeleso.selectedItem.toString()
            elektrokotel = binding.cbTopElektrokotel.isChecked
            elektrokotelTypPos = binding.spTopElektrokotel.selectedItemPosition
            elektrokotelTyp = binding.spTopElektrokotel.selectedItem.toString()
            plynKotel = binding.cbTopPlynKotel.isChecked
            plynKotelTypPos = binding.spTopPlynKotel.selectedItemPosition
            plynKotelTyp = binding.spTopPlynKotel.selectedItem.toString()
            krb = binding.cbTopKrb.isChecked
            krbTypPos = binding.spTopKrb.selectedItemPosition
            krbTyp = binding.spTopKrb.selectedItem.toString()
            jiny = binding.cbTopJiny.isChecked
            ktery = binding.etTopJiny.editText!!.text.toString()
        }

        stranky.zdrojeTv.apply {
            topneTeleso = binding.cbTvTopneTeleso.isChecked
            topneTelesoTypPos = binding.spTvTopneTeleso.selectedItemPosition
            topneTelesoTyp = binding.spTvTopneTeleso.selectedItem.toString()
            elektrokotel = binding.cbTvElektrokotel.isChecked
            plynKotel = binding.cbTvPlynKotel.isChecked
            krb = binding.cbTvKrb.isChecked
            jiny = binding.cbTvJiny.isChecked
            ktery = binding.etTvJiny.editText!!.text.toString()
            poznamka = binding.etPoznamka6.editText!!.text.toString()
        }

        if (stranky.zdrojeTop == Stranky.ZdrojeTop()) return
        if (stranky.zdrojeTv == Stranky.ZdrojeTv()) return

        requireContext().saver.save(stranky)
    }

    private lateinit var binding: FragmentZdrojeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentZdrojeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun update() {

        binding.spTopTopneTeleso.visibility = if (binding.cbTopTopneTeleso.isChecked) View.VISIBLE else View.GONE

        binding.spTopElektrokotel.visibility = if (binding.cbTopElektrokotel.isChecked) View.VISIBLE else View.GONE

        binding.spTopPlynKotel.visibility = if (binding.cbTopPlynKotel.isChecked) View.VISIBLE else View.GONE

        binding.spTopKrb.visibility = if (binding.cbTopKrb.isChecked) View.VISIBLE else View.GONE

        binding.spTvTopneTeleso.visibility = if (binding.cbTvTopneTeleso.isChecked) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter1 = ArrayAdapter.createFromResource(requireActivity(), R.array.nove, android.R.layout.simple_spinner_item)
        val adapter2 = ArrayAdapter.createFromResource(requireActivity(), R.array.novy, android.R.layout.simple_spinner_item)

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spTopTopneTeleso.adapter = adapter1
        binding.spTopElektrokotel.adapter = adapter2
        binding.spTopPlynKotel.adapter = adapter2
        binding.spTopKrb.adapter = adapter2


        val adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.kamTeleso, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spTvTopneTeleso.adapter = adapter

        update()

        binding.cbTopTopneTeleso.setOnClickListener { update(); save() }
        binding.cbTopElektrokotel.setOnClickListener { update(); save() }
        binding.cbTopPlynKotel.setOnClickListener { update(); save() }
        binding.cbTopKrb.setOnClickListener { update(); save() }
        binding.cbTopJiny.setOnClickListener { save() }

        binding.tvTopTopneTeleso.setOnClickListener {
            binding.cbTopTopneTeleso.isChecked = !binding.cbTopTopneTeleso.isChecked
            update(); save()
        }
        binding.tvTopElektrokotel.setOnClickListener {
            binding.cbTopElektrokotel.isChecked = !binding.cbTopElektrokotel.isChecked
            update(); save()
        }
        binding.tvTopPlynKotel.setOnClickListener {
            binding.cbTopPlynKotel.isChecked = !binding.cbTopPlynKotel.isChecked
            update(); save()
        }
        binding.tvTopKrb.setOnClickListener {
            binding.cbTopKrb.isChecked = !binding.cbTopKrb.isChecked
            update(); save()
        }

        binding.cbTvTopneTeleso.setOnClickListener { update(); save() }
        binding.cbTvElektrokotel.setOnClickListener { update() }
        binding.cbTvJiny.setOnClickListener { update() }
        binding.cbTvKrb.setOnClickListener { update() }
        binding.cbTvPlynKotel.setOnClickListener { update() }

        binding.tvTvTopneTeleso.setOnClickListener {
            binding.cbTvTopneTeleso.isChecked = !binding.cbTvTopneTeleso.isChecked
            update(); save()
        }
        binding.tvTvElektrokotel.setOnClickListener {
            binding.cbTvElektrokotel.isChecked = !binding.cbTvElektrokotel.isChecked
            update(); save()
        }
        binding.tvTvPlynKotel.setOnClickListener {
            binding.cbTvPlynKotel.isChecked = !binding.cbTvPlynKotel.isChecked
            update(); save()
        }
        binding.tvTvKrb.setOnClickListener {
            binding.cbTvKrb.isChecked = !binding.cbTvKrb.isChecked
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
            binding.spTopTopneTeleso.onItemSelectedListener = it
            binding.spTopElektrokotel.onItemSelectedListener = it
            binding.spTopElektrokotel.onItemSelectedListener = it
            binding.spTopKrb.onItemSelectedListener = it
            binding.spTvTopneTeleso.onItemSelectedListener = it
        }

        binding.etTopJiny.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etTvJiny.editText!!.doOnTextChanged { _, _, _, _ -> save() }
        binding.etPoznamka6.editText!!.doOnTextChanged { _, _, _, _ -> save() }

        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {

            binding.cbTopTopneTeleso.isChecked = stranky.zdrojeTop.topneTeleso
            binding.spTopTopneTeleso.setSelection(stranky.zdrojeTop.topneTelesoTypPos)
            binding.cbTopElektrokotel.isChecked = stranky.zdrojeTop.elektrokotel
            binding.spTopElektrokotel.setSelection(stranky.zdrojeTop.elektrokotelTypPos)
            binding.cbTopPlynKotel.isChecked = stranky.zdrojeTop.plynKotel
            binding.spTopElektrokotel.setSelection(stranky.zdrojeTop.plynKotelTypPos)
            binding.cbTopKrb.isChecked = stranky.zdrojeTop.krb
            binding.spTopKrb.setSelection(stranky.zdrojeTop.krbTypPos)
            binding.cbTopJiny.isChecked = stranky.zdrojeTop.jiny
            binding.etTopJiny.editText!!.setText(stranky.zdrojeTop.ktery)

            binding.cbTvTopneTeleso.isChecked = stranky.zdrojeTv.topneTeleso
            binding.spTvTopneTeleso.setSelection(stranky.zdrojeTv.topneTelesoTypPos)
            binding.cbTvElektrokotel.isChecked = stranky.zdrojeTv.elektrokotel
            binding.cbTvPlynKotel.isChecked = stranky.zdrojeTv.plynKotel
            binding.cbTvKrb.isChecked = stranky.zdrojeTv.krb
            binding.cbTvJiny.isChecked = stranky.zdrojeTv.jiny
            binding.etTvJiny.editText!!.setText(stranky.zdrojeTv.ktery)
            binding.etPoznamka6.editText!!.setText(stranky.zdrojeTv.poznamka)

            update()

        }
    }
}
