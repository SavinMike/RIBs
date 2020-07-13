package com.badoo.ribs.sandbox.rib.recycler.builder

import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.sandbox.rib.dialog_example.DialogExample
import com.badoo.ribs.sandbox.rib.foo_bar.FooBar
import com.badoo.ribs.sandbox.rib.hello_world.HelloWorld
import com.badoo.ribs.sandbox.rib.recycler.Recycler
import com.badoo.ribs.sandbox.rib.recycler.RecyclerNode
import dagger.BindsInstance

@RecyclerScope
@dagger.Component(
    modules = [
        RecyclerModule::class,
        RecyclerHostModule::class
    ],
    dependencies = [Recycler.Dependency::class]
)
internal interface RecyclerComponent :
    FooBar.Dependency,
    HelloWorld.Dependency,
    DialogExample.Dependency {

    @dagger.Component.Factory
    interface Factory {
        fun create(
            dependency: Recycler.Dependency,
            @BindsInstance customisation: Recycler.Customisation,
            @BindsInstance buildParams: BuildParams<Nothing?>
        ): RecyclerComponent
    }

    fun node(): RecyclerNode
}
