package com.regulus.dotaznik.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.regulus.dotaznik.*
import com.regulus.dotaznik.activities.PrihlaseniActivity
import com.regulus.dotaznik.databinding.FragmentDetailObjektuBinding
import java.util.*

class DetailObjektuFragment : Fragment() {

    private var timer = Timer()
    override fun onStop() {
        super.onStop()
        timer.cancel()
        timer = Timer()
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

        // saving
        val task = object : TimerTask() {
            override fun run() {

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

                if (stranky.detailObjektu == Stranky.DetailObjektu()) return

                requireContext().saver.save(stranky)
            }
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

        timer.scheduleAtFixedRate(task, 0, 200)
    }
}