package com.kyhsgeekcode.dereinfo

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SortAlertDialogFragment : DialogFragment() {

    private lateinit var onClickListener: DialogInterface.OnClickListener
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.sort)
                .setItems(R.array.sort_conditions, onClickListener)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            onClickListener = context as DialogInterface.OnClickListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement OnClickDialogListener"))
        }
    }
}