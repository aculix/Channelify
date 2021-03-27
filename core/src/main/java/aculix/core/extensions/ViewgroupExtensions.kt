package aculix.core.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// Inflates the layout in RecyclerView Adapter
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes,this, false);
}