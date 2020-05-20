package com.badoo.ribs.sandbox.rib.recycler.builder

import com.badoo.ribs.core.builder.BuildParams
import com.badoo.ribs.core.builder.SimpleBuilder
import com.badoo.ribs.sandbox.rib.recycler.Recycler
import com.badoo.ribs.sandbox.rib.recycler.RecyclerNode

class RecyclerBuilder(
    private val dependency: Recycler.Dependency
) : SimpleBuilder<Recycler>() {

    override fun build(buildParams: BuildParams<Nothing?>): RecyclerNode =
        DaggerRecyclerComponent
            .factory()
            .create(
                dependency = dependency,
                customisation = buildParams.getOrDefault(Recycler.Customisation()),
                buildParams = buildParams
            )
            .node()
}
