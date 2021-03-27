package aculix.core.extensions

import com.google.android.material.chip.Chip

fun Chip.check() {
    isChecked = true
}

fun Chip.unCheck() {
    isChecked = false
}