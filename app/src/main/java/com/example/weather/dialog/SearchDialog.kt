package com.example.weather.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.weather.R
import com.example.weather.utils.NetworkHelper
import com.example.weather.utils.PreferenceHelper
import com.google.android.material.textfield.TextInputLayout

class SearchDialog: DialogFragment() {

    private lateinit var mCity: TextInputLayout
    private lateinit var mDays: TextInputLayout
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val dialog: View = requireActivity().layoutInflater.inflate(R.layout.search_dialog, null)

        mCity = dialog.findViewById(R.id.city_edt_dialog)
        mDays = dialog.findViewById(R.id.days_edt_dialog)

        val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
        val cityPref = pref.getString(PreferenceHelper.CITY_KEY, "Hanoi")
        val daysPref = pref.getString(PreferenceHelper.DAYS_KEY, "16")

        mCity.editText!!.setText(cityPref)
        mDays.editText!!.setText(daysPref)

        builder.setView(dialog).setPositiveButton("Save", object : OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                val city: String
                if (mCity.editText!!.text.toString().equals("")) {
                    city = "Hanoi"
                } else {
                    city = mCity.editText!!.text.toString()
                }
                var days: String
                if (mDays.editText!!.text.toString().equals("")) {
                    days = "16"
                } else {
                    days = mDays.editText!!.text.toString()
                    if (days.toInt() > 16) {
                        days = "16"
                    } else if (days.toInt() < 3){
                        days = "3"
                    }
                }
                if (!NetworkHelper.isNetworkAvailable(requireContext())) {
                    Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
                    return
                }
                (activity as SaveListener).save(clearString(city), clearString(days))
            }

        }).setNegativeButton("Cancel", object : OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {

            }
        })

        return builder.create()
    }

    private fun clearString(s: String): String {
        var ret = ""
        var temp = s.toCharArray()
        for (i in 0..s.length-1) {
            if (temp[i] != ' ') {
                ret += temp[i]
            }
        }
        return ret
    }

    interface SaveListener {
        fun save(city: String, days: String)
    }
}