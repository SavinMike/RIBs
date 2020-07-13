package com.badoo.ribs.sandbox.rib.recycler.recycler

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badoo.ribs.android.recyclerview.LayoutManagerFactory
import com.badoo.ribs.android.recyclerview.RecyclerViewFactory
import com.badoo.ribs.android.recyclerview.RecyclerViewHost
import com.badoo.ribs.android.recyclerview.client.ViewHolderLayoutProvider
import com.badoo.ribs.sandbox.rib.recycler.recycler.model.RecyclerItem
import com.google.android.material.appbar.AppBarLayout
import java.lang.ref.WeakReference

class RecyclerViewProvider : RecyclerViewHost.RecyclerViewDependency<RecyclerItem> {

    private var recyclerView: WeakReference<RecyclerView>? = null

    private var recyclerViewProvider: WeakReference<(RecyclerView) -> Unit>? = null

    fun setRecyclerViewProvider(func: (RecyclerView) -> Unit) {
        recyclerViewProvider = WeakReference(func)
        recyclerView?.get()?.let(func)
    }

    override fun layoutManagerFactory(): LayoutManagerFactory =
        { LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false) }

    override fun recyclerViewFactory(): RecyclerViewFactory = {
        val recyclerView = RecyclerView(it)
        recyclerView.layoutParams = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            .apply {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        recyclerView.also {
            this.recyclerView = WeakReference(it)
            recyclerViewProvider?.get()?.invoke(it)
        }
    }

    override fun viewHolderLayoutParams(): ViewHolderLayoutProvider<RecyclerItem> =
        object : ViewHolderLayoutProvider<RecyclerItem> {
            override fun invoke(viewType: Int): FrameLayout.LayoutParams =
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
        }
}
