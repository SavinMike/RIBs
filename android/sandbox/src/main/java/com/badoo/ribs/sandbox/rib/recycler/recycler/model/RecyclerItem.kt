package com.badoo.ribs.sandbox.rib.recycler.recycler.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class RecyclerItem : Parcelable {
    @Parcelize
    object HelloWorld: RecyclerItem()

    @Parcelize
    object FooBar: RecyclerItem()

    @Parcelize
    object DialogExample: RecyclerItem()
}