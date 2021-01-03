package com.kyhsgeekcode.dereinfo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_sort.view.*


class SortAlertDialogFragment : DialogFragment() {
    interface SortDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment?, item: Int, ascending: Boolean)
        fun onDialogNegativeClick(dialog: DialogFragment) {}
    }

    lateinit var sortDialogListener: SortDialogListener
    lateinit var inflated: View
    var sortTypeIndex: Int = 0
    var sortOrderAsc: Boolean = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            inflated = inflater.inflate(R.layout.dialog_sort, null)
            builder.setTitle(R.string.sort)
                .setView(inflated)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    sortTypeIndex = inflated.sortSpinner.selectedItemPosition
                    sortOrderAsc = inflated.sortRBAscending.isChecked
                    sortDialogListener.onDialogPositiveClick(
                        this,
                        sortTypeIndex,
                        sortOrderAsc
                    )
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                    sortDialogListener.onDialogNegativeClick(this)
                }
            inflated.sortRBAscending.isChecked = true
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                context!!,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.sort_conditions)
            )
            inflated.sortSpinner.adapter = adapter
            inflated.sortSpinner.setSelection(sortTypeIndex)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            sortDialogListener = context as SortDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement SortDialogListener")
            )
        }
    }
}
