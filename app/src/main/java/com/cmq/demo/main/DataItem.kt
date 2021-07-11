package com.cmq.demo.main

import android.content.Context

data class DataItem(
        val name: String,
        val desc: String,
        val action: (context: Context) -> Unit
)