package com.badoo.ribs.android.recyclerview

import android.content.Context
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.badoo.ribs.android.recyclerview.RecyclerViewHost.Input
import com.badoo.ribs.android.recyclerview.client.ViewHolderLayoutProvider
import com.badoo.ribs.annotation.ExperimentalApi
import com.badoo.ribs.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.routing.resolver.RoutingResolver
import io.reactivex.ObservableSource
import kotlinx.android.parcel.Parcelize

@ExperimentalApi
interface RecyclerViewHost<T : Parcelable> : Rib, Connectable<Input<T>, Nothing> {

    @ExperimentalApi
    interface Dependency<T : Parcelable> : RecyclerViewDependency<T> {
        fun hostingStrategy(): HostingStrategy
        fun initialElements(): List<T>
        fun recyclerViewHostInput(): ObservableSource<Input<T>>
        fun resolver(): RoutingResolver<T>
    }

    @ExperimentalApi
    interface RecyclerViewDependency<T : Parcelable> {
        fun recyclerViewFactory(): RecyclerViewFactory
        fun layoutManagerFactory(): LayoutManagerFactory
        fun viewHolderLayoutParams(): ViewHolderLayoutProvider<T>
    }

    @ExperimentalApi
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

    @ExperimentalApi
    sealed class Input<T : Parcelable> : Parcelable {
        @Parcelize
        data class Add<T : Parcelable>(val element: T) : Input<T>()
    }
}

typealias RecyclerViewFactory = (Context) -> RecyclerView

typealias LayoutManagerFactory = (Context) -> RecyclerView.LayoutManager
