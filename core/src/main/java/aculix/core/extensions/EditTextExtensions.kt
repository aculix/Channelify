package aculix.core.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Adds TextWatcher to the EditText
 */
fun EditText.onTextChanged(listener: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            listener(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // We only need the value of EditText after the user stops typing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // We only need the value of EditText after the user stops typing
        }
    })
}