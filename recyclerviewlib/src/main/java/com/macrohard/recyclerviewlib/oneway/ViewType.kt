package com.macrohard.recyclerviewlib.oneway

import androidx.annotation.LayoutRes

/**
 *  Different view types for [RecyclerView]
 */
interface ViewType<out T> {

    @LayoutRes
    fun layoutId(): Int

    fun data(): T

    fun isUserInteractionEnabled() = true
}