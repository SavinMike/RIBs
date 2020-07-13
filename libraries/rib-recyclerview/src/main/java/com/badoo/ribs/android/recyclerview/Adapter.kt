package com.badoo.ribs.android.recyclerview

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.badoo.ribs.android.recyclerview.RecyclerViewHost.HostingStrategy.EAGER
import com.badoo.ribs.android.recyclerview.RecyclerViewHost.HostingStrategy.LAZY
import com.badoo.ribs.android.recyclerview.RecyclerViewHost.Input
import com.badoo.ribs.android.recyclerview.RecyclerViewHostFeature.State.Entry
import com.badoo.ribs.android.recyclerview.client.ViewHolderLayoutProvider
import com.badoo.ribs.annotation.ExperimentalApi
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.plugin.NodeLifecycleAware
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.activator.ChildActivator
import com.badoo.ribs.routing.source.impl.Pool
import com.badoo.ribs.util.RIBs.errorHandler
import io.reactivex.functions.Consumer
import java.lang.ref.WeakReference

@ExperimentalApi
internal class Adapter<T : Parcelable>(
    private val hostingStrategy: RecyclerViewHost.HostingStrategy,
    initialEntries: List<Entry<T>>? = null,
    private val routingSource: Pool<T>,
    private val feature: RecyclerViewHostFeature<T>,
    private val viewHolderLayoutParams: ViewHolderLayoutProvider<T>
) : RecyclerView.Adapter<Adapter.ViewHolder>(),
    Consumer<RecyclerViewHostFeature.State<T>>,
    NodeLifecycleAware,
    ChildActivator<T> {

    private val holders: MutableMap<Routing.Identifier, WeakReference<ViewHolder>> = mutableMapOf()
    private val addedItems: MutableList<Routing.Identifier> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var identifier: Routing.Identifier? = null
    }

    private var items: List<Entry<T>> = initialEntries ?: emptyList()

    override fun getItemCount(): Int =
        items.size

    override fun accept(state: RecyclerViewHostFeature.State<T>) {
        items = state.items

        when (state.lastCommand) {
            null -> { /* No-op when restored from TimeCapsule or genuinely empty state */ }
            is Input.Add -> {
                eagerAdd(state.items.last())
                notifyItemInserted(state.items.lastIndex)
            }
        }
    }

    private fun eagerAdd(entry: Entry<T>) {
        if (hostingStrategy == EAGER) {
            addToRouter(entry.element, entry.identifier)
        }
    }

    override fun onAttach(nodeLifecycle: Lifecycle) {
        items.forEach(::eagerAdd)
    }

    override fun onDetach() {
        addedItems.forEach {
            routingSource.deactivate(it)
        }
    }

    override fun getItemId(position: Int): Long {
        return viewHolderLayoutParams.getItemId(items[position].element, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            FrameLayout(parent.context).apply {
                layoutParams = viewHolderLayoutParams(viewType)
            }
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = items[position]
        holder.identifier = entry.identifier
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val identifier = holder.identifier!! // at this point it should be bound
        holders[identifier] = WeakReference(holder)

        if (hostingStrategy == LAZY) {
            val entry = feature.state.items.find { it.identifier == identifier }!!
            addToRouter(entry.element, entry.identifier)
        }

        routingSource.activate(identifier)
    }

    override fun getItemViewType(position: Int): Int {
        return viewHolderLayoutParams.provideViewType(items[position].element, position)
    }

    override fun activate(routing: Routing<T>, child: Node<*>) {
        holders[routing.identifier]?.get()?.let { holder ->
            child.attachToView(holder.itemView as FrameLayout)
        } ?: errorHandler.handleNonFatalError("Holder is gone! Routing: $routing, child: $child")
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.identifier?.let { identifier ->
            routingSource.deactivate(identifier)
            if (hostingStrategy == LAZY) {
                removeFromRouter(identifier)
            }
        } ?: errorHandler.handleNonFatalError("Holder is not bound! holder: $holder")
    }

    override fun deactivate(routing: Routing<T>, child: Node<*>) {
        child.saveViewState()
        child.detachFromView()
    }

    private fun removeFromRouter(identifier: Routing.Identifier) {
        addedItems.remove(identifier)
        routingSource.remove(identifier)
    }

    private fun addToRouter(element: T, identifier: Routing.Identifier) {
        addedItems.add(identifier)
        routingSource.add(element, identifier)
    }
}
