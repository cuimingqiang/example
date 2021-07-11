package com.cmq.base

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView


inline fun <reified T : View> Activity.bind(@IdRes id: Int): Lazy<T> = lazy {
    findViewById(id)
}

inline fun <reified T : View> Fragment.bind(@IdRes id: Int): Lazy<T?> = lazy {
    view?.findViewById(id)
}

inline fun <reified T : View> Dialog.bind(@IdRes id: Int): Lazy<T?> = lazy {
    findViewById(id)
}

inline fun <reified T : View> ViewGroup.bind(@IdRes id: Int): Lazy<T> = lazy {
    findViewById(id)
}

inline fun <reified T : View> RecyclerView.ViewHolder.bind(@IdRes id: Int): Lazy<T> = lazy {
    itemView.findViewById(id)
}

val Float.dp: Float
    get() = android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
    ).toInt()