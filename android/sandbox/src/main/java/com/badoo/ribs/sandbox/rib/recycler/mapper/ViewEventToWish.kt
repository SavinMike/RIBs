package com.badoo.ribs.sandbox.rib.recycler.mapper

import com.badoo.ribs.sandbox.rib.recycler.RecyclerView.Event
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature.Wish

internal object ViewEventToWish : (Event) -> Wish? {

    override fun invoke(event: Event): Wish? =
        TODO("Implement RecyclerViewEventToWish mapping")
}
