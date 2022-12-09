package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view.component

import android.content.Context
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup

/**
 * UI component
 * Let the user select a storage capacity
 */
class StorageCapacityRadioGroup {
    private var context: Context
    private var radioGroup: RadioGroup
    fun getView(): RadioGroup {
        return radioGroup
    }

    constructor(context: Context) {
        this.context = context
        radioGroup = RadioGroup(context)
        radioGroup.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun addStorageCapacity(name: String) {
        val radioButton = RadioButton(context)
        radioButton.text = name
        radioGroup.addView(radioButton)
    }
}