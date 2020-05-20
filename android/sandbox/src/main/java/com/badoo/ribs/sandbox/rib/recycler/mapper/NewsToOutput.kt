package com.badoo.ribs.sandbox.rib.recycler.mapper

import com.badoo.ribs.sandbox.rib.recycler.Recycler.Output
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature.News

internal object NewsToOutput : (News) -> Output? {

    override fun invoke(news: News): Output? =
        TODO("Implement RecyclerNewsToOutput mapping")
}
