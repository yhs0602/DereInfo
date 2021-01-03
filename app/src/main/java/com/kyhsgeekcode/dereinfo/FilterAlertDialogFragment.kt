package com.kyhsgeekcode.dereinfo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_filter.view.*


class FilterAlertDialogFragment(initMap: HashMap<Int, Boolean>?) : DialogFragment() {
    lateinit var filterDialogListener: FilterDialogListener
    lateinit var inflated: View
    val checkedMap = initMap ?: HashMap()
    val CHECKED_KEY = "Checkeds"

    interface FilterDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment?, checked: Map<Int, Boolean>)
        fun onDialogNegativeClick(dialog: DialogFragment) {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            inflated = inflater.inflate(R.layout.dialog_filter, null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflated)
                // Add action buttons
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    checkedMap[R.id.filterCBTypeAllCheck] =
                        inflated.filterCBTypeAllCheck.isChecked
                    checkedMap[R.id.filterCBCute] = inflated.filterCBCute.isChecked
                    checkedMap[R.id.filterCBCool] = inflated.filterCBCool.isChecked
                    checkedMap[R.id.filterCBPassion] = inflated.filterCBPassion.isChecked
                    checkedMap[R.id.filterCBAllType] = inflated.filterCBAllType.isChecked
                    checkedMap[R.id.filterCBMasterPlus] = inflated.filterCBMasterPlus.isChecked
                    checkedMap[R.id.filterCBGrand] = inflated.filterCBGrand.isChecked
                    checkedMap[R.id.filterCBSmart] = inflated.filterCBSmart.isChecked
                    checkedMap[R.id.filterCBStarred] = inflated.filterCBStarred.isChecked
                    filterDialogListener.onDialogPositiveClick(this, checkedMap)
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                    filterDialogListener.onDialogNegativeClick(this)
                }
            val result = builder.create()

            inflated.filterCBTypeAllCheck.setOnCheckedChangeListener { button, isChecked ->
                if (button.isPressed) {
                    inflated.filterCBCute.isChecked = isChecked
                    inflated.filterCBCool.isChecked = isChecked
                    inflated.filterCBPassion.isChecked = isChecked
                    inflated.filterCBAllType.isChecked = isChecked
                    checkedMap[R.id.filterCBTypeAllCheck] = isChecked
                }
            }
            inflated.filterCBCute.isChecked = checkedMap[R.id.filterCBCute] ?: true
            inflated.filterCBCool.isChecked = checkedMap[R.id.filterCBCool] ?: true
            inflated.filterCBPassion.isChecked = checkedMap[R.id.filterCBPassion] ?: true
            inflated.filterCBAllType.isChecked = checkedMap[R.id.filterCBAllType] ?: true
            inflated.filterCBTypeAllCheck.isChecked = checkedMap[R.id.filterCBTypeAllCheck] ?: true
            inflated.filterCBMasterPlus.isChecked = checkedMap[R.id.filterCBMasterPlus] ?: false
            inflated.filterCBGrand.isChecked = checkedMap[R.id.filterCBGrand] ?: false
            inflated.filterCBStarred.isChecked = checkedMap[R.id.filterCBStarred] ?: false
            inflated.filterCBSmart.isChecked = checkedMap[R.id.filterCBSmart] ?: false

            val allCheckNotifier = CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (!isChecked)
                    inflated.filterCBTypeAllCheck.isChecked = false
            }
            with(inflated) {
                filterCBCute.setOnCheckedChangeListener(allCheckNotifier)
                filterCBCool.setOnCheckedChangeListener(allCheckNotifier)
                filterCBPassion.setOnCheckedChangeListener(allCheckNotifier)
                filterCBAllType.setOnCheckedChangeListener(allCheckNotifier)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(CHECKED_KEY, checkedMap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            checkedMap.putAll(savedInstanceState.getSerializable(CHECKED_KEY) as HashMap<Int, Boolean>)
        }
    }
}


