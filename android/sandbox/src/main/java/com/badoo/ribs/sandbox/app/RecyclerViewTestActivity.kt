package com.badoo.ribs.sandbox.app

import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.android.PermissionRequester
import com.badoo.ribs.android.RibActivity
import com.badoo.ribs.android.recyclerview.RecyclerViewHost.Input
import com.badoo.ribs.android.recyclerview.client.RecyclerViewRibResolver
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.builder.BuildContext
import com.badoo.ribs.core.builder.BuildContext.Companion.root
import com.badoo.ribs.core.routing.action.AddToRecyclerViewRoutingAction.Companion.recyclerView
import com.badoo.ribs.core.routing.action.AttachRibRoutingAction
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.portal.Portal
import com.badoo.ribs.core.routing.portal.PortalBuilder
import com.badoo.ribs.core.routing.portal.PortalRouter
import com.badoo.ribs.core.routing.transition.handler.CrossFader
import com.badoo.ribs.core.routing.transition.handler.Slider
import com.badoo.ribs.core.routing.transition.handler.TransitionHandler
import com.badoo.ribs.dialog.DialogLauncher
import com.badoo.ribs.sandbox.R
import com.badoo.ribs.sandbox.rib.foo_bar.FooBar
import com.badoo.ribs.sandbox.rib.foo_bar.FooBarBuilder
import com.badoo.ribs.sandbox.rib.lorem_ipsum.LoremIpsum
import com.badoo.ribs.sandbox.rib.lorem_ipsum.LoremIpsumBuilder
import com.badoo.ribs.sandbox.rib.recycler.Recycler
import com.badoo.ribs.sandbox.rib.recycler.builder.RecyclerBuilder
import com.badoo.ribs.sandbox.rib.switcher.Switcher
import com.badoo.ribs.sandbox.rib.switcher.builder.SwitcherBuilder
import com.badoo.ribs.sandbox.util.CoffeeMachine
import com.badoo.ribs.sandbox.util.StupidCoffeeMachine
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import kotlinx.android.parcel.Parcelize

/** The sample app's single activity */
class RecyclerViewTestActivity : RibActivity() {

    // We'll put these into the RecyclerView by resolving them to builders (see below)
    sealed class Item : Parcelable {
        @Parcelize object LoremIpsumItem : Item()
        @Parcelize object FooBarItem : Item()
        @Parcelize object Switcher : Item()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_root)
        super.onCreate(savedInstanceState)
    }

    override val rootViewGroup: ViewGroup
        get() = findViewById(R.id.root)

    private val fooBarBuilder = FooBarBuilder(object : FooBar.Dependency {
        override fun foobarInput(): ObservableSource<FooBar.Input> = Observable.empty()
        override fun foobarOutput(): Consumer<FooBar.Output> = Consumer {}
        override fun permissionRequester(): PermissionRequester = this@RecyclerViewTestActivity.permissionRequester
    })

    private val loremIpsumBuilder = LoremIpsumBuilder(object : LoremIpsum.Dependency {
        override fun loremIpsumOutput(): Consumer<LoremIpsum.Output> = Consumer { }
    })

    private val noopPortal = object : Portal.OtherSide {
        override fun showContent(remoteRouter: Router<*, *, *, *, *>, remoteConfiguration: Parcelable) {
            // Sorry, no-op
        }

        override fun showOverlay(remoteRouter: Router<*, *, *, *, *>, remoteConfiguration: Parcelable) {
            // Sorry, no-op
        }
    }

    private val switcherBuilder = SwitcherBuilder(
        object : Switcher.Dependency {
            override fun activityStarter(): ActivityStarter = activityStarter
            override fun permissionRequester(): PermissionRequester =
                permissionRequester

            override fun dialogLauncher(): DialogLauncher = this@RecyclerViewTestActivity
            override fun coffeeMachine(): CoffeeMachine = StupidCoffeeMachine()
            override fun portal(): Portal.OtherSide = noopPortal
        }
    )

    private val ribResolver = object : RecyclerViewRibResolver<Item> {
        override fun resolve(element: Item): RoutingAction =
            when (element) {
                Item.LoremIpsumItem -> recyclerView { loremIpsumBuilder.build(it) }
                Item.FooBarItem -> recyclerView { fooBarBuilder.build(it) }
                Item.Switcher-> recyclerView { switcherBuilder.build(it) }
            }
    }

    private val initialElements = listOf(
        Item.FooBarItem
    )

    private val inputCommands = Observable.just<Input<Item>>(
        Input.Add(Item.LoremIpsumItem),
        Input.Add(Item.Switcher)
    )

    override fun createRib(savedInstanceState: Bundle?): Rib =
        PortalBuilder(
            object : Portal.Dependency {
                override fun defaultRoutingAction(): (Portal.OtherSide) -> RoutingAction = { portal ->
                    AttachRibRoutingAction.attach { buildRecyclerNode(portal, it) }
                }

                override fun transitionHandler(): TransitionHandler<PortalRouter.Configuration>? =
                    TransitionHandler.multiple(
                        Slider { it.configuration is PortalRouter.Configuration.Content },
                        CrossFader { it.configuration is PortalRouter.Configuration.Overlay }
                    )

                private fun buildRecyclerNode(portal: Portal.OtherSide, buildContext: BuildContext): Recycler =
                    RecyclerBuilder(
                        object : Recycler.Dependency {
                            override fun permissionRequester(): PermissionRequester =
                                permissionRequester

                            override fun activityStarter(): ActivityStarter =
                                activityStarter

                            override fun portal(): Portal.OtherSide =
                                portal

                            override fun dialogLauncher(): DialogLauncher =
                                this@RecyclerViewTestActivity
                        }
                    ).build(buildContext)
            }
        ).build(root(savedInstanceState, AppRibCustomisations))
//        RecyclerViewHostBuilder(
//            object : RecyclerViewHost.Dependency<Item> {
//                override fun hostingStrategy(): RecyclerViewHost.HostingStrategy = EAGER
//                override fun initialElements(): List<Item> = initialElements
//                override fun recyclerViewHostInput(): ObservableSource<Input<Item>> = inputCommands
//                override fun resolver(): RecyclerViewRibResolver<Item> = ribResolver
//                override fun recyclerViewFactory(): RecyclerViewFactory = ::RecyclerView
//                override fun layoutManagerFactory(): LayoutManagerFactory = ::LinearLayoutManager
//                override fun viewHolderLayoutParams(): ViewHolderLayoutProvider<Item> = object : ViewHolderLayoutProvider<Item> {}
//            }
//        ).build(root(savedInstanceState))
}
