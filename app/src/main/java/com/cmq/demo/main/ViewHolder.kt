package com.cmq.demo.main

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmq.demo.R

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name = itemView.findViewById<TextView>(R.id.name)
    private val desc = itemView.findViewById<TextView>(R.id.desc)
    private lateinit var item: DataItem

    init {
        itemView.setOnClickListener {
            item.action.invoke(it.context)
        }
    }

    fun bindData(item: DataItem) {
        this.item = item
        name.text = item.name
        desc.text = item.desc
    }
}