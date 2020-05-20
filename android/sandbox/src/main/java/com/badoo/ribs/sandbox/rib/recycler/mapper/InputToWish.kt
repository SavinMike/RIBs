package com.badoo.ribs.sandbox.rib.recycler.mapper

import com.badoo.ribs.sandbox.rib.recycler.Recycler.Input
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature.Wish

internal object InputToWish : (Input) -> Wish? {

    override fun invoke(event: Input): Wish? =
        TODO("Implement RecyclerInputToWish mapping")
}
