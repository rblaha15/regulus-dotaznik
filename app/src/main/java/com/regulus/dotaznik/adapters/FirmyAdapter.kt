package com.regulus.dotaznik.adapters

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.regulus.dotaznik.R
import com.regulus.dotaznik.activities.FirmyActivity

class FirmyAdapter(private val originalDataSet: List<String>, private val activity: FirmyActivity, private val callback: (String) -> Unit) : RecyclerView.Adapter<FirmyAdapter.ViewHolder>() {

    private var dataSet: List<String> = originalDataSet

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvFirma: TextView = view.findViewById(R.id.tvFirma)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.firma, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvFirma.text = dataSet[position]


        holder.view.setOnClickListener {
            callback(dataSet[position])
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun filter(text: String) {

        dataSet = originalDataSet.filter { it.uppercase().contains(text.uppercase()) }

        notifyDataSetChanged()

    }

}