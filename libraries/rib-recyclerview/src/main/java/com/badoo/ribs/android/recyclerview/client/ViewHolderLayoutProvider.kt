package com.badoo.ribs.android.recyclerview.client

import android.widget.FrameLayout

interface ViewHolderLayoutProvider<T> : (Int) -> FrameLayout.LayoutParams {

    override fun invoke(viewType: Int): FrameLayout.LayoutParams =
        FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

    fun provideViewType(item: T, position: Int): Int =
        0

    fun getItemId(item: T, position: Int): Long =
        -1
}