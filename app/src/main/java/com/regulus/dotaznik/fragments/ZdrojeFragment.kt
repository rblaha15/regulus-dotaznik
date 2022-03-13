package com.regulus.dotaznik.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.regulus.dotaznik.*
import com.regulus.dotaznik.activities.MainActivity
import com.regulus.dotaznik.databinding.FragmentZdrojeBinding
import java.util.*


class ZdrojeFragment : Fragment() {

    private var timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
        timer = Timer()
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

        binding.fabOdeslat.setOnLongClickListener {

            (activity as MainActivity).debugMode = true
            (activity as MainActivity).odeslat()
            return@setOnLongClickListener true
        }

        binding.fabOdeslat.setOnClickListener {

            (activity as MainActivity).debugMode = false
            (activity as MainActivity).odeslat()
        }

        binding.cbTopTopneTeleso.setOnClickListener { update() }
        binding.cbTopElektrokotel.setOnClickListener { update() }
        binding.cbTopPlynKotel.setOnClickListener { update() }
        binding.cbTopKrb.setOnClickListener { update() }

        binding.tvTopTopneTeleso.setOnClickListener {
            binding.cbTopTopneTeleso.isChecked = !binding.cbTopTopneTeleso.isChecked
            update()
        }
        binding.tvTopElektrokotel.setOnClickListener {
            binding.cbTopElektrokotel.isChecked = !binding.cbTopElektrokotel.isChecked
            update()
        }
        binding.tvTopPlynKotel.setOnClickListener {
            binding.cbTopPlynKotel.isChecked = !binding.cbTopPlynKotel.isChecked
            update()
        }
        binding.tvTopKrb.setOnClickListener {
            binding.cbTopKrb.isChecked = !binding.cbTopKrb.isChecked
            update()
        }

        val task = object : TimerTask() {
            override fun run() {

                val stranky = requireContext().saver.get()

                stranky.zdrojeTop.apply {
                    topTopneTeleso = binding.cbTopTopneTeleso.isChecked
                    topTopneTelesoTypPos = binding.spTopTopneTeleso.selectedItemPosition
                    topTopneTelesoTyp = binding.spTopTopneTeleso.selectedItem.toString()
                    topElektrokotel = binding.cbTopElektrokotel.isChecked
                    topElektrokotelTypPos = binding.spTopElektrokotel.selectedItemPosition
                    topElektrokotelTyp = binding.spTopElektrokotel.selectedItem.toString()
                    topPlynKotel = binding.cbTopPlynKotel.isChecked
                    topPlynKotelTypPos = binding.spTopPlynKotel.selectedItemPosition
                    topPlynKotelTyp = binding.spTopPlynKotel.selectedItem.toString()
                    topKrb = binding.cbTopKrb.isChecked
                    topKrbTypPos = binding.spTopKrb.selectedItemPosition
                    topKrbTyp = binding.spTopKrb.selectedItem.toString()
                    topJiny = binding.cbTopJiny.isChecked
                    topKtery = binding.etTopJiny.editText!!.text.toString()
                }

                stranky.zdrojeTv.apply {
                    tvTopneTeleso = binding.cbTvTopneTeleso.isChecked
                    tvTopneTelesoTypPos = binding.spTvTopneTeleso.selectedItemPosition
                    tvTopneTelesoTyp = binding.spTvTopneTeleso.selectedItem.toString()
                    tvElektrokotel = binding.cbTvElektrokotel.isChecked
                    tvPlynKotel = binding.cbTvPlynKotel.isChecked
                    tvKrb = binding.cbTvKrb.isChecked
                    tvJiny = binding.cbTvJiny.isChecked
                    tvKtery = binding.etTvJiny.editText!!.text.toString()
                    poznamka = binding.etPoznamka6.editText!!.text.toString()
                }

                if (stranky.zdrojeTop == Stranky.ZdrojeTop()) return
                if (stranky.zdrojeTv == Stranky.ZdrojeTv()) return

                requireContext().saver.save(stranky)
            }
        }

        val stranky = requireContext().saver.get()

        requireActivity().runOnUiThread {

            binding.cbTopTopneTeleso.isChecked = stranky.zdrojeTop.topTopneTeleso
            binding.spTopTopneTeleso.setSelection(stranky.zdrojeTop.topTopneTelesoTypPos)
            binding.cbTopElektrokotel.isChecked = stranky.zdrojeTop.topElektrokotel
            binding.spTopElektrokotel.setSelection(stranky.zdrojeTop.topElektrokotelTypPos)
            binding.cbTopPlynKotel.isChecked = stranky.zdrojeTop.topPlynKotel
            binding.spTopElektrokotel.setSelection(stranky.zdrojeTop.topPlynKotelTypPos)
            binding.cbTopKrb.isChecked = stranky.zdrojeTop.topKrb
            binding.spTopKrb.setSelection(stranky.zdrojeTop.topKrbTypPos)
            binding.cbTopJiny.isChecked = stranky.zdrojeTop.topJiny
            binding.etTopJiny.editText!!.setText(stranky.zdrojeTop.topKtery)

            binding.cbTvTopneTeleso.isChecked = stranky.zdrojeTv.tvTopneTeleso
            binding.spTvTopneTeleso.setSelection(stranky.zdrojeTv.tvTopneTelesoTypPos)
            binding.cbTvElektrokotel.isChecked = stranky.zdrojeTv.tvElektrokotel
            binding.cbTvPlynKotel.isChecked = stranky.zdrojeTv.tvPlynKotel
            binding.cbTvKrb.isChecked = stranky.zdrojeTv.tvKrb
            binding.cbTvJiny.isChecked = stranky.zdrojeTv.tvJiny
            binding.etTvJiny.editText!!.setText(stranky.zdrojeTv.tvKtery)
            binding.etPoznamka6.editText!!.setText(stranky.zdrojeTv.poznamka)

            update()

        }

        timer.scheduleAtFixedRate(task, 0, 200)

        binding.cbTvTopneTeleso.setOnClickListener { update() }


        binding.tvTvTopneTeleso.setOnClickListener {
            binding.cbTvTopneTeleso.isChecked = !binding.cbTvTopneTeleso.isChecked
            update()
        }
        binding.tvTvElektrokotel.setOnClickListener {
            binding.cbTvElektrokotel.isChecked = !binding.cbTvElektrokotel.isChecked
            update()
        }
        binding.tvTvPlynKotel.setOnClickListener {
            binding.cbTvPlynKotel.isChecked = !binding.cbTvPlynKotel.isChecked
            update()
        }
        binding.tvTvKrb.setOnClickListener {
            binding.cbTvKrb.isChecked = !binding.cbTvKrb.isChecked
            update()
        }
    }
}