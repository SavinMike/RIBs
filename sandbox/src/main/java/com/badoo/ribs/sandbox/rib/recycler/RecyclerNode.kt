package com.badoo.ribs.sandbox.rib.recycler

import android.view.ViewGroup
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature
import io.reactivex.Single

class RecyclerNode internal constructor(
    buildParams: BuildParams<*>,
    viewFactory: ((ViewGroup) -> RecyclerView?)?,
    private val router: RecyclerRouter,
    private val feature: RecyclerFeature,
    private val interactor: RecyclerInteractor
) : Node<RecyclerView>(
    buildParams = buildParams,
    viewFactory = viewFactory,
    plugins = listOf(router, interactor)
), Recycler
