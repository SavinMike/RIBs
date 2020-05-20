package com.badoo.ribs.sandbox.rib.recycler

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.mvicore.binder.using
import com.badoo.ribs.core.Interactor
import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature
import com.badoo.ribs.sandbox.rib.recycler.mapper.StateToViewModel
import com.badoo.ribs.sandbox.rib.recycler.mapper.ViewEventToWish

internal class RecyclerInteractor(
    buildParams: BuildParams<*>,
    private val router: RecyclerRouter,
    private val feature: RecyclerFeature
) : Interactor<RecyclerView>(
    buildParams = buildParams,
    disposables = feature
) {

    override fun onViewCreated(view: RecyclerView, viewLifecycle: Lifecycle) {
        viewLifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using ViewEventToWish)
        }
    }
}
