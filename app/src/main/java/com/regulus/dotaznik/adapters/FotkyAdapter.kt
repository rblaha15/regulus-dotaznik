package com.regulus.dotaznik.adapters

import android.content.Context
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.regulus.dotaznik.R
import com.regulus.dotaznik.activities.FotkyActivity
import java.io.File

class FotkyAdapter(private val activity: FotkyActivity) : RecyclerView.Adapter<FotkyAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFotka: ImageView = view.findViewById(R.id.ivFotka)
        val btnOdstranit: Button = view.findViewById(R.id.btnOdstranit)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.fotka, viewGroup, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val sharedPref = activity.getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)

        val file = File(activity.filesDir, "photo${position + 1}.jpg")

        try {
            val imageUri = FileProvider.getUriForFile(activity, "${activity.applicationContext.packageName}.provider", file)

            Log.i("foto", file.absolutePath)
            Log.i("foto", imageUri.path!!)

            val bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                MediaStore.Images.Media.getBitmap(activity.contentResolver, imageUri)
            }
            else {
                val source = ImageDecoder.createSource(activity.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }
            viewHolder.ivFotka.setImageBitmap(bitmap)

        } catch (e: Throwable) {


            MaterialAlertDialogBuilder(activity).apply {
                setTitle("Něco se pokazilo!")

                setMessage("Podrobnější informace:\n\n$position\n\n${e.stackTraceToString()}\n\n${activity.filesDir}")
            }

            return
        }



        viewHolder.btnOdstranit.setOnClickListener {

            val s = sharedPref.getInt("fotky", 0)

            file.delete()
            this.notifyItemRemoved(position)

            for (i in position+1 until s) {
                val file1 = File(activity.filesDir, "photo${i+1}.jpg")
                val file2 = File(activity.filesDir, "photo${i}.jpg")

                file1.renameTo(file2)
            }

            sharedPref.edit().putInt("fotky", s-1).apply()

        }


    }


    override fun getItemCount(): Int {

        val sharedPref = activity.getSharedPreferences("PREFS_DOTAZNIK", Context.MODE_PRIVATE)

        return sharedPref.getInt("fotky", 0)
    }

}


