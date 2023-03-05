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
import java.text.Normalizer
import java.util.regex.Pattern

class SearchDialog: DialogFragment() {

    private lateinit var mCity: TextInputLayout

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val dialog: View = requireActivity().layoutInflater.inflate(R.layout.search_dialog, null)

        mCity = dialog.findViewById(R.id.city_edt_dialog)


        val pref = PreferenceHelper.getPref(requireContext(), PreferenceHelper.PREF_KEY)
        val cityPref = pref.getString(PreferenceHelper.CITY_NAME, "Hanoi")

        mCity.editText!!.setText(cityPref)

        builder.setView(dialog).setPositiveButton("Save", object : OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                val city: String
                if (mCity.editText!!.text.toString().equals("")) {
                    city = "Hanoi"
                } else {
                    city = mCity.editText!!.text.toString()
                }
                if (!NetworkHelper.isNetworkAvailable(requireContext())) {
                    Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
                    return
                }
                (activity as SaveListener).save(clearString(city))
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
        return deAccent(ret).toString()
    }

    fun deAccent(str: String?): String? {
        val nfdNormalizedString: String = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("")
    }

    interface SaveListener {
        fun save(city: String)
    }
}