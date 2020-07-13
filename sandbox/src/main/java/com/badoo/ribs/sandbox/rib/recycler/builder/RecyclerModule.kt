@file:SuppressWarnings("LongParameterList", "LongMethod")

package com.badoo.ribs.sandbox.rib.recycler.builder

import com.badoo.ribs.android.recyclerview.RecyclerViewHostBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.sandbox.rib.recycler.Recycler
import com.badoo.ribs.sandbox.rib.recycler.RecyclerInteractor
import com.badoo.ribs.sandbox.rib.recycler.RecyclerNode
import com.badoo.ribs.sandbox.rib.recycler.RecyclerRouter
import com.badoo.ribs.sandbox.rib.recycler.RecyclerView
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature
import com.badoo.ribs.sandbox.rib.recycler.recycler.RecyclerDependency
import com.badoo.ribs.sandbox.rib.recycler.recycler.RecyclerViewProvider
import dagger.Provides

@dagger.Module
internal object RecyclerModule {

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun router(
        // pass component to child rib builders, or remove if there are none
        component: RecyclerComponent,
        buildParams: BuildParams<Nothing>,
        interactor: RecyclerInteractor,
        customisation: Recycler.Customisation,
        dependency: RecyclerDependency
    ): RecyclerRouter =
        RecyclerRouter(
            buildParams = buildParams,
            recyclerViewHostBuilder = RecyclerViewHostBuilder(dependency),
            routingSource = interactor
        )

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun feature(tabs: Tabs): RecyclerFeature =
        RecyclerFeature(tabs.list)

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun interactor(
        dependency: Recycler.Dependency,
        buildParams: BuildParams<Nothing>,
        feature: RecyclerFeature
    ): RecyclerInteractor =
        RecyclerInteractor(
            buildParams = buildParams,
            feature = feature
        )

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun node(
        buildParams: BuildParams<Nothing>,
        customisation: Recycler.Customisation,
        router: RecyclerRouter,
        interactor: RecyclerInteractor,
        viewDependency: RecyclerView.ViewDependency,
        feature: RecyclerFeature
    ): RecyclerNode = RecyclerNode(
        buildParams = buildParams,
        viewFactory = customisation.viewFactory(viewDependency),
        router = router,
        interactor = interactor,
        feature = feature
    )

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun viewDependency(
        recyclerViewProvider: RecyclerViewProvider
    ): RecyclerView.ViewDependency =
        object : RecyclerView.ViewDependency {
            override val recyclerViewProvider: RecyclerViewProvider
                get() = recyclerViewProvider
        }
}
