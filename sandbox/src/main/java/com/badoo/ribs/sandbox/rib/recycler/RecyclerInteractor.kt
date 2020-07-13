package com.badoo.ribs.sandbox.rib.recycler

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.mvicore.binder.using
import com.badoo.ribs.clienthelper.interactor.BackStackInteractor
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature
import com.badoo.ribs.sandbox.rib.recycler.mapper.StateToViewModel
import com.badoo.ribs.sandbox.rib.recycler.mapper.ViewEventToWish

internal class RecyclerInteractor(
    buildParams: BuildParams<*>,
    private val feature: RecyclerFeature
) : BackStackInteractor<Recycler, RecyclerView, RecyclerRouter.Configuration>(
    buildParams = buildParams,
    disposables = feature,
    initialConfiguration = RecyclerRouter.Configuration.Content.Default
) {

    override fun onViewCreated(view: RecyclerView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
        }
    }
}
