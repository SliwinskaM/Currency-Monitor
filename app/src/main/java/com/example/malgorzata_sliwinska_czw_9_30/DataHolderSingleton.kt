package com.example.malgorzata_sliwinska_czw_9_30

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley.newRequestQueue

object DataHolderSingleton {
    lateinit var queue: RequestQueue
    fun prepare(context: Context) {
        queue = newRequestQueue(context)
    }


}