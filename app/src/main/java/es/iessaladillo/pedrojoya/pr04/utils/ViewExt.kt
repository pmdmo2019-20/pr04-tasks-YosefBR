package es.iessaladillo.pedrojoya.pr04.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// NO TOCAR

// Hace que la vista esté invisible a no ser que se cumpla la condición,
// en cuyo caso estará visible.
fun View.invisibleUnless(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.INVISIBLE
}
