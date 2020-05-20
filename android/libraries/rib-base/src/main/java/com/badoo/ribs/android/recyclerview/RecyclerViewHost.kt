package com.badoo.ribs.android.recyclerview

import android.content.Context
import android.os.Parcelable
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.badoo.ribs.android.recyclerview.client.RecyclerViewRibResolver
import com.badoo.ribs.android.recyclerview.client.ViewHolderLayoutProvider
import com.badoo.ribs.core.Rib
import io.reactivex.ObservableSource
import kotlinx.android.parcel.Parcelize

/**
 * Considered experimental. Handle with care.
 */
interface RecyclerViewHost<T : Parcelable> : Rib {

    interface Dependency<T : Parcelable> : RecyclerViewDependency<T> {
        fun hostingStrategy(): HostingStrategy
        fun initialElements(): List<T>
        fun recyclerViewHostInput(): ObservableSource<Input<T>>
        fun resolver(): RecyclerViewRibResolver<T>
    }

    interface RecyclerViewDependency<T : Parcelable> {
        fun recyclerViewFactory(): RecyclerViewFactory
        fun layoutManagerFactory(): LayoutManagerFactory
        fun viewHolderLayoutParams(): ViewHolderLayoutProvider<T>
    }

    enum class HostingStrategy {
        /**
         * Child RIBs get created immediately and are only destroyed along with host
         */
        EAGER,

        /**
         * Child RIBs get created when their associated ViewHolders are attached, and get destroyed
         * along with them
         */
        LAZY
    }

    sealed class Input<T : Parcelable> : Parcelable {
        @Parcelize
        data class Add<T : Parcelable>(val element: T) : Input<T>()

//        @Parcelize
//        data class Remove<T : Parcelable>(val element: T) : Input<T>()
//
//        @Parcelize
//        object Clear : Input<Nothing>()
    }
}

typealias RecyclerViewFactory = (Context) -> RecyclerView

typealias LayoutManagerFactory = (Context) -> RecyclerView.LayoutManager
