package com.badoo.ribs.sandbox.rib.recycler

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.PagerSnapHelper
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.customisation.inflate
import com.badoo.ribs.sandbox.R
import com.badoo.ribs.sandbox.rib.recycler.RecyclerView.Event
import com.badoo.ribs.sandbox.rib.recycler.RecyclerView.ViewModel
import com.badoo.ribs.sandbox.rib.recycler.recycler.LinearLayoutManagerRecyclerViewItemResolver
import com.badoo.ribs.sandbox.rib.recycler.recycler.RecyclerViewProvider
import com.badoo.ribs.sandbox.rib.recycler.recycler.TabLayoutRecyclerMediator
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import androidx.recyclerview.widget.RecyclerView as AndroidRecyclerView

interface RecyclerView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event

    data class ViewModel(
        val tabs: List<String>
    )

    interface ViewDependency {
        val recyclerViewProvider: RecyclerViewProvider
    }

    interface Factory : ViewFactory<ViewDependency, RecyclerView>
}

class RecyclerViewImpl private constructor(
    override val androidView: ViewGroup,
    private val recyclerViewProvider: RecyclerViewProvider,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : RecyclerView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    private val tabs: TabLayout = androidView.findViewById(R.id.tabs)
    private var mediator: TabLayoutRecyclerMediator? = null
    private var recyclerView: AndroidRecyclerView? = null

    class Factory(
        @LayoutRes private val layoutRes: Int = R.layout.rib_recycler
    ) : RecyclerView.Factory {
        override fun invoke(deps: RecyclerView.ViewDependency): (ViewGroup) -> RecyclerView = {
            RecyclerViewImpl(
                inflate(it, layoutRes),
                deps.recyclerViewProvider
            )
        }
    }

    init {
        recyclerViewProvider.setRecyclerViewProvider {
            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(it)
            recyclerView = it
        }
    }

    override fun accept(vm: RecyclerView.ViewModel) {
        updateTabs(vm)
    }

    private fun updateTabs(vm: ViewModel) {
        mediator?.detach()
        recyclerView?.let {
            mediator = TabLayoutRecyclerMediator(
                tabLayout = tabs,
                recyclerView = it,
                recyclerViewItemResolver = LinearLayoutManagerRecyclerViewItemResolver(),
                tabConfigurationStrategy = { tab, position ->
                    tab.text = vm.tabs[position]
                }
            ).apply { attach() }
        }
    }

    override fun getParentViewForChild(child: Node<*>): ViewGroup? =
        androidView.findViewById(R.id.content)
}
