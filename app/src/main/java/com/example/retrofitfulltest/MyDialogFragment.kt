package com.example.retrofitfulltest

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyDialogFragment : DialogFragment () {

    //MyDialogFragment().show(parentFragmentManager, MyDialogFragment.TAG)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireContext().let {
            AlertDialog.Builder(it)
                .setTitle("Title")
                .setMessage("Message")
                .setPositiveButton("Yes"){dialog, button ->

                }
                .setNegativeButton("No"){dialog, button ->

                }
                .setNeutralButton("Cancel") {dialog, button ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()

        }
    }

    companion object {
        const val TAG = "MyDialogFragment"
    }
}