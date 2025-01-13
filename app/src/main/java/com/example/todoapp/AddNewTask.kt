
package com.example.todoapp

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.todoapp.DialogCloseListener
import com.example.todoapp.Model.ToDoModel
import com.example.todoapp.R
import com.example.todoapp.Utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AddNewTask : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ActionBottomDialog"
        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }

    private lateinit var newTaskText: EditText
    private lateinit var newTaskSaveButton: Button
    private lateinit var db: DatabaseHandler
    private lateinit var chipGroupPriority: ChipGroup

    private var selectedPriority: String = "Low"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newTaskText = view.findViewById(R.id.newTaskText)
        newTaskSaveButton = view.findViewById(R.id.newtaskbtn)
        chipGroupPriority = view.findViewById(R.id.chip_group_priority)

        var isUpdate = false

        val bundle = arguments
        if (bundle != null) {
            isUpdate = true
            val task = bundle.getString("task")
            val priority = bundle.getInt("priority", 0)
            newTaskText.setText(task)

            // Set the selected priority chip based on the task
            val chipId = when (priority) {
                2 -> R.id.chip_high
                1 -> R.id.chip_medium
                else -> R.id.chip_low
            }
            chipGroupPriority.check(chipId)

            if (!task.isNullOrEmpty()) {
                newTaskSaveButton.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
            }
        }

        db = DatabaseHandler(requireActivity())
        db.openDatabase()

        newTaskText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    newTaskSaveButton.isEnabled = false
                    newTaskSaveButton.setTextColor(Color.GRAY)
                } else {
                    newTaskSaveButton.isEnabled = true
                    newTaskSaveButton.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        chipGroupPriority.post {
            chipGroupPriority.setOnCheckedStateChangeListener { group, checkedIds ->
                // Get the first selected chip ID (or null if no chip is selected)
                val selectedChipId = checkedIds.firstOrNull()

                if (selectedChipId != null) {
                    val selectedChip = group.findViewById<Chip>(selectedChipId)
                    selectedPriority = when (selectedChip?.id) {
                        R.id.chip_high -> "High"
                        R.id.chip_medium -> "Medium"
                        else -> "Low"
                    }
                } else {
                    selectedPriority = "Low"
                }

                // Log the selected priority for debugging
                Log.d("PrioritySelection", "Selected Priority: $selectedPriority")
            }
        }


        newTaskSaveButton.setOnClickListener {
            val text = newTaskText.text.toString()
            if (text.isNotEmpty()) {
                val priorityValue = getPriorityValue(selectedPriority)
                if (isUpdate && bundle != null) {
                    db.updateTask(bundle?.getInt("id") ?: -1, text)
                    db.updatePriority(bundle.getInt("id"), priorityValue)
                } else {
                    val task = ToDoModel().apply {
                        this.task = text
                        this.status = 0
                        this.priority = priorityValue
                    }
                    db.insertTask(task)
                }
                Log.d("PrioritySelection", "Selected Priority: $selectedPriority, Value: $priorityValue")
            }
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        val activity = activity
        if (activity is DialogCloseListener) {
            activity.handleDialogClose(dialog)
        }
    }

    private fun getPriorityValue(priority: String): Int {
        return when (priority) {
            "High" -> 2
            "Medium" -> 1
            else -> 0
        }
    }

    private fun setPrioritySelection(priority: String) {
        val chipId = when (priority) {
            "High" -> R.id.chip_high
            "Medium" -> R.id.chip_medium
            else -> R.id.chip_low
        }
        chipGroupPriority.check(chipId)
    }
}
