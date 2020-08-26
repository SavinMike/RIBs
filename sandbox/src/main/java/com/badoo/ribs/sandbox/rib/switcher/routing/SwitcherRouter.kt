package com.badoo.ribs.sandbox.rib.switcher.routing

import android.os.Parcelable
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.dialog.routing.resolution.DialogResolution.Companion.showDialog
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.ChildResolution.Companion.attach
import com.badoo.ribs.routing.resolution.CompositeResolution.Companion.composite
import com.badoo.ribs.routing.resolution.InvokeOnExecute.Companion.execute
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.router.Router
import com.badoo.ribs.routing.source.RoutingSource
import com.badoo.ribs.routing.source.RoutingSource.Companion.permanent
import com.badoo.ribs.routing.transition.handler.TransitionHandler
import com.badoo.ribs.sandbox.rib.menu.Menu
import com.badoo.ribs.sandbox.rib.menu.Menu.Input.SelectMenuItem
import com.badoo.ribs.sandbox.rib.menu.Menu.MenuItem
import com.badoo.ribs.sandbox.rib.switcher.dialog.DialogToTestOverlay
import com.badoo.ribs.sandbox.rib.switcher.routing.SwitcherRouter.Configuration
import com.badoo.ribs.sandbox.rib.switcher.routing.SwitcherRouter.Configuration.Content
import com.badoo.ribs.sandbox.rib.switcher.routing.SwitcherRouter.Configuration.Overlay
import com.badoo.ribs.sandbox.rib.switcher.routing.SwitcherRouter.Configuration.Permanent
import com.jakewharton.rxrelay2.PublishRelay
import kotlinx.android.parcel.Parcelize

class SwitcherRouter internal constructor(
    buildParams: BuildParams<Nothing?>,
    routingSource: RoutingSource<Configuration>,
    transitionHandler: TransitionHandler<Configuration>? = null,
    private val builders: SwitcherChildBuilders,
    private val dialogLauncher: DialogLauncher,
    private val dialogToTestOverlay: DialogToTestOverlay
): Router<Configuration>(
    buildParams = buildParams,
    routingSource = routingSource + permanent(Permanent.Menu),
    transitionHandler = transitionHandler
) {
    sealed class Configuration : Parcelable {
        sealed class Permanent : Configuration() {
            @Parcelize object Menu : Permanent()
        }
        sealed class Content : Configuration() {
            @Parcelize object Hello : Content()
            @Parcelize object Foo : Content()
            @Parcelize object DialogsExample : Content()
            @Parcelize object Compose : Content()
            @Parcelize object Blocker : Content()
        }
        sealed class Overlay : Configuration() {
            @Parcelize object Dialog : Overlay()
        }
    }

    internal val menuUpdater = PublishRelay.create<Menu.Input>()

    override fun resolve(routing: Routing<Configuration>): Resolution =
        with(builders) {
            when (routing.configuration) {
                is Permanent.Menu -> attach { menu.build(it) }
                is Content.Hello -> composite(
                    attach { helloWorld.build(it) },
                    execute { menuUpdater.accept(SelectMenuItem(MenuItem.HelloWorld)) }
                )
                is Content.Foo -> composite(
                    attach { fooBar.build(it) },
                    execute { menuUpdater.accept(SelectMenuItem(MenuItem.FooBar)) }
                )
                is Content.DialogsExample -> composite(
                    attach {  dialogExample.build(it) },
                    execute { menuUpdater.accept(SelectMenuItem(MenuItem.Dialogs)) }
                )
                is Content.Compose -> composite(
                    attach { composeParent.build(it) },
                    execute { menuUpdater.accept(SelectMenuItem(MenuItem.Compose)) }
                )
                is Content.Blocker -> attach { blocker.build(it) }
                is Overlay.Dialog -> showDialog(routingSource, routing.identifier, dialogLauncher, dialogToTestOverlay)
            }
        }
}

