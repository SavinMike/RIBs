package com.badoo.ribs.sandbox.app

import android.os.Bundle
import android.view.ViewGroup
import com.badoo.ribs.android.RibActivity
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildContext
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import com.badoo.ribs.portal.Portal
import com.badoo.ribs.portal.PortalBuilder
import com.badoo.ribs.portal.PortalRouter
import com.badoo.ribs.routing.action.AttachRibRoutingAction
import com.badoo.ribs.routing.action.RoutingAction
import com.badoo.ribs.routing.transition.handler.CrossFader
import com.badoo.ribs.routing.transition.handler.Slider
import com.badoo.ribs.routing.transition.handler.TransitionHandler
import com.badoo.ribs.sandbox.R
import com.badoo.ribs.sandbox.rib.recycler.Recycler
import com.badoo.ribs.sandbox.rib.recycler.builder.RecyclerBuilder

/** The sample app's single activity */
class RecyclerViewTestActivity : RibActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_root)
        super.onCreate(savedInstanceState)
    }

    override val rootViewGroup: ViewGroup
        get() = findViewById(R.id.root)

    override fun createRib(savedInstanceState: Bundle?): Rib =
        PortalBuilder(
            object : Portal.Dependency {
                override fun defaultRoutingAction(): (Portal.OtherSide) -> RoutingAction = { portal ->
                    AttachRibRoutingAction.attach { buildRecyclerNode(portal, it) }
                }

                override fun transitionHandler(): TransitionHandler<PortalRouter.Configuration>? =
                    null

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
}
