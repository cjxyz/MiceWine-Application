package com.nianticlabs.pokemongo.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.nianticlabs.pokemongo.R

class InfoDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_info, null)

        view.findViewById<TextView>(R.id.titleText).text = titleText
        view.findViewById<TextView>(R.id.descriptionText).text = descriptionText
        view.findViewById<MaterialButton>(R.id.okButton).apply {
            text = getString(android.R.string.ok)

            setOnClickListener {
                dismiss()
            }
        }

        return AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialog).setView(view).create()
    }

    companion object {
        var titleText = ""
        var descriptionText = ""
    }
}