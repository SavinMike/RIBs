package com.badoo.ribs.sandbox.rib.recycler

import android.os.Parcelable
import com.badoo.ribs.android.recyclerview.RecyclerViewHostBuilder
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.routing.action.AttachRibRoutingAction.Companion.attach
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.transition.handler.TransitionHandler
import com.badoo.ribs.sandbox.rib.recycler.RecyclerRouter.Configuration
import com.badoo.ribs.sandbox.rib.recycler.RecyclerRouter.Configuration.Content
import com.badoo.ribs.sandbox.rib.recycler.RecyclerRouter.Configuration.Overlay
import com.badoo.ribs.sandbox.rib.recycler.RecyclerRouter.Configuration.Permanent
import com.badoo.ribs.sandbox.rib.recycler.recycler.model.RecyclerItem
import kotlinx.android.parcel.Parcelize

class RecyclerRouter(
    buildParams: BuildParams<*>,
    transitionHandler: TransitionHandler<Configuration>? = null,
    private val recyclerViewHostBuilder: RecyclerViewHostBuilder<RecyclerItem>
) : Router<Configuration, Permanent, Content, Overlay, RecyclerView>(
    buildParams = buildParams,
    transitionHandler = transitionHandler,
    initialConfiguration = Content.Default,
    permanentParts = emptyList()
) {
    sealed class Configuration : Parcelable {
        sealed class Permanent : Configuration()
        sealed class Content : Configuration() {
            @Parcelize
            object Default : Content()
        }

        sealed class Overlay : Configuration()
    }

    override fun resolveConfiguration(configuration: Configuration): RoutingAction =
        when (configuration) {
            is Content.Default -> attach { recyclerViewHostBuilder.build(it) }
        }
}
