package com.badoo.ribs.sandbox.rib.recycler.recycler

import com.badoo.ribs.android.recyclerview.RecyclerViewHost
import com.badoo.ribs.android.recyclerview.client.RecyclerViewRibResolver
import com.badoo.ribs.sandbox.rib.recycler.recycler.model.RecyclerItem
import io.reactivex.ObservableSource

class RecyclerDependency(
    recyclerViewDependency: RecyclerViewHost.RecyclerViewDependency<RecyclerItem>,
    private val input: ObservableSource<RecyclerViewHost.Input<RecyclerItem>>,
    private val resolver: RecyclerResolver,
    private val initialElements: List<RecyclerItem> = emptyList()
) : RecyclerViewHost.Dependency<RecyclerItem>, RecyclerViewHost.RecyclerViewDependency<RecyclerItem> by recyclerViewDependency {

    override fun hostingStrategy(): RecyclerViewHost.HostingStrategy =
        RecyclerViewHost.HostingStrategy.LAZY

    override fun initialElements(): List<RecyclerItem> =
        initialElements

    override fun recyclerViewHostInput(): ObservableSource<RecyclerViewHost.Input<RecyclerItem>> =
        input

    override fun resolver(): RecyclerViewRibResolver<RecyclerItem> =
        resolver
}