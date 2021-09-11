package com.kyhsgeekcode.dereinfo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.kyhsgeekcode.dereinfo.databinding.DialogFilterBinding


class FilterAlertDialogFragment(initMap: HashMap<Int, Boolean>?) : DialogFragment() {
    lateinit var filterDialogListener: FilterDialogListener
    val checkedMap = initMap ?: HashMap()
    val CHECKED_KEY = "Checkeds"

    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!


    interface FilterDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment?, checked: Map<Int, Boolean>)
        fun onDialogNegativeClick(dialog: DialogFragment) {}
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
//            val inflater = requireActivity().layoutInflater
//            binding = inflater.inflate(R.layout.dialog_filter, null)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            _binding = DialogFilterBinding.inflate(LayoutInflater.from(context))

            builder.setView(binding.root)
                // Add action buttons
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    checkedMap[R.id.filterCBTypeAllCheck] =
                        binding.filterCBTypeAllCheck.isChecked
                    checkedMap[R.id.filterCBCute] = binding.filterCBCute.isChecked
                    checkedMap[R.id.filterCBCool] = binding.filterCBCool.isChecked
                    checkedMap[R.id.filterCBPassion] = binding.filterCBPassion.isChecked
                    checkedMap[R.id.filterCBAllType] = binding.filterCBAllType.isChecked
                    checkedMap[R.id.filterCBMasterPlus] = binding.filterCBMasterPlus.isChecked
                    checkedMap[R.id.filterCBGrand] = binding.filterCBGrand.isChecked
                    checkedMap[R.id.filterCBSmart] = binding.filterCBSmart.isChecked
                    checkedMap[R.id.filterCBStarred] = binding.filterCBStarred.isChecked
                    filterDialogListener.onDialogPositiveClick(this, checkedMap)
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                    filterDialogListener.onDialogNegativeClick(this)
                }
            val result = builder.create()

            binding.filterCBTypeAllCheck.setOnCheckedChangeListener { button, isChecked ->
                if (button.isPressed) {
                    binding.filterCBCute.isChecked = isChecked
                    binding.filterCBCool.isChecked = isChecked
                    binding.filterCBPassion.isChecked = isChecked
                    binding.filterCBAllType.isChecked = isChecked
                    checkedMap[R.id.filterCBTypeAllCheck] = isChecked
                }
            }
            binding.filterCBCute.isChecked = checkedMap[R.id.filterCBCute] ?: true
            binding.filterCBCool.isChecked = checkedMap[R.id.filterCBCool] ?: true
            binding.filterCBPassion.isChecked = checkedMap[R.id.filterCBPassion] ?: true
            binding.filterCBAllType.isChecked = checkedMap[R.id.filterCBAllType] ?: true
            binding.filterCBTypeAllCheck.isChecked = checkedMap[R.id.filterCBTypeAllCheck] ?: true
            binding.filterCBMasterPlus.isChecked = checkedMap[R.id.filterCBMasterPlus] ?: false
            binding.filterCBGrand.isChecked = checkedMap[R.id.filterCBGrand] ?: false
            binding.filterCBStarred.isChecked = checkedMap[R.id.filterCBStarred] ?: false
            binding.filterCBSmart.isChecked = checkedMap[R.id.filterCBSmart] ?: false

            val allCheckNotifier = CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (!isChecked)
                    binding.filterCBTypeAllCheck.isChecked = false
            }
            with(binding) {
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


