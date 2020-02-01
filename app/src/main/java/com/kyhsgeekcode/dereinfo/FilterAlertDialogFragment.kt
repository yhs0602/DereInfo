package com.kyhsgeekcode.dereinfo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_filter.view.*

class FilterAlertDialogFragment : DialogFragment() {
    lateinit var filterDialogListener: FilterDialogListener
    var inflated: View? = null
    val checkedMap = HashMap<Int,Boolean>()
    interface FilterDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment,checked: Map<Int, Boolean>)
        fun onDialogNegativeClick(dialog: DialogFragment) {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            inflated = inflater.inflate(R.layout.dialog_filter, null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflated)
                // Add action buttons
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    checkedMap[R.id.filterCBTypeAllCheck] = inflated!!.filterCBTypeAllCheck.isChecked
                    checkedMap[R.id.filterCBCute] = inflated!!.filterCBCute.isChecked
                    checkedMap[R.id.filterCBCool] = inflated!!.filterCBCool.isChecked
                    checkedMap[R.id.filterCBPassion] = inflated!!.filterCBPassion.isChecked
                    checkedMap[R.id.filterCBAllType] = inflated!!.filterCBAllType.isChecked
                    checkedMap[R.id.filterCBMasterPlus] = inflated!!.filterCBMasterPlus.isChecked
                    filterDialogListener.onDialogPositiveClick(this, checkedMap)
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { _, _ ->
                    dialog.cancel()
                    filterDialogListener.onDialogNegativeClick(this)
                }
            val result = builder.create()

            inflated!!.filterCBTypeAllCheck.setOnCheckedChangeListener { _, isChecked ->
                inflated!!.filterCBCute.isChecked = isChecked
                inflated!!.filterCBCool.isChecked = isChecked
                inflated!!.filterCBPassion.isChecked = isChecked
                inflated!!.filterCBAllType.isChecked = isChecked
                checkedMap[R.id.filterCBTypeAllCheck] = isChecked
            }
            result
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            filterDialogListener = context as FilterDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }
}


