package com.badoo.ribs.sandbox.rib.recycler

import android.os.Parcelable
import com.badoo.ribs.android.recyclerview.RecyclerViewHostBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.action.AttachRibRoutingAction.Companion.attach
import com.badoo.ribs.routing.action.RoutingAction
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.RoutingSource
import com.badoo.ribs.routing.transition.handler.TransitionHandler
import com.badoo.ribs.sandbox.rib.recycler.RecyclerRouter.Configuration
import com.badoo.ribs.sandbox.rib.recycler.RecyclerRouter.Configuration.Content
import com.badoo.ribs.sandbox.rib.recycler.recycler.model.RecyclerItem
import kotlinx.android.parcel.Parcelize

class RecyclerRouter(
    buildParams: BuildParams<*>,
    transitionHandler: TransitionHandler<Configuration>? = null,
    routingSource: RoutingSource<Configuration>,
    private val recyclerViewHostBuilder: RecyclerViewHostBuilder<RecyclerItem>
) : Router<Configuration>(
    buildParams = buildParams,
    transitionHandler = transitionHandler,
    routingSource = routingSource
) {
    sealed class Configuration : Parcelable {
        sealed class Permanent : Configuration()

        sealed class Content : Configuration() {
            @Parcelize
            object Default : Content()
        }

        sealed class Overlay : Configuration()
    }

    override fun resolve(routing: Routing<Configuration>): RoutingAction =
        when (routing.configuration) {
            is Content.Default -> attach { recyclerViewHostBuilder.build(it) }
        }
}
