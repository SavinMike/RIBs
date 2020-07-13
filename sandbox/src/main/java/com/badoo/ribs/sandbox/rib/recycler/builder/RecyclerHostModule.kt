package com.badoo.ribs.sandbox.rib.recycler.builder

import com.badoo.ribs.android.recyclerview.RecyclerViewHost
import com.badoo.ribs.sandbox.rib.dialog_example.builder.DialogExampleBuilder
import com.badoo.ribs.sandbox.rib.foo_bar.FooBar
import com.badoo.ribs.sandbox.rib.foo_bar.FooBarBuilder
import com.badoo.ribs.sandbox.rib.hello_world.HelloWorld
import com.badoo.ribs.sandbox.rib.hello_world.HelloWorldBuilder
import com.badoo.ribs.sandbox.rib.recycler.recycler.RecyclerDependency
import com.badoo.ribs.sandbox.rib.recycler.recycler.RecyclerResolver
import com.badoo.ribs.sandbox.rib.recycler.recycler.RecyclerViewProvider
import com.badoo.ribs.sandbox.rib.recycler.recycler.model.RecyclerItem
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import dagger.Provides
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

@dagger.Module
internal object RecyclerHostModule {

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun recyclerObservableSource(
        relay: PublishRelay<RecyclerViewHost.Input<RecyclerItem>>
    ): ObservableSource<RecyclerViewHost.Input<RecyclerItem>> =
        relay

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun recyclerRelay(): PublishRelay<RecyclerViewHost.Input<RecyclerItem>> =
        PublishRelay.create()

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun recyclerViewRibResolver(component: RecyclerComponent): RecyclerResolver =
        RecyclerResolver(
            HelloWorldBuilder(component),
            FooBarBuilder(component),
            DialogExampleBuilder(component)
        )

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun recyclerDependency(
        recyclerResolver: RecyclerResolver,
        recyclerViewProvider: RecyclerViewProvider,
        input: ObservableSource<RecyclerViewHost.Input<RecyclerItem>>,
        tabs: Tabs
    ): RecyclerDependency =
        RecyclerDependency(
            recyclerViewDependency = recyclerViewProvider,
            resolver = recyclerResolver,
            input = input,
            initialElements = tabs.list
        )

    @RecyclerScope
    @Provides
    @JvmStatic
    internal fun recyclerViewProvider(): RecyclerViewProvider =
        RecyclerViewProvider()

    @RecyclerScope
    @Provides
    @JvmStatic
    fun foobarInput(relay: Relay<FooBar.Input>): ObservableSource<FooBar.Input> =
        relay

    @RecyclerScope
    @Provides
    @JvmStatic
    fun foobarInputRelay(): Relay<FooBar.Input> =
        PublishRelay.create()

    @RecyclerScope
    @Provides
    @JvmStatic
    fun foobarOutput(relay: Relay<FooBar.Output>): Consumer<FooBar.Output> =
        relay

    @RecyclerScope
    @Provides
    @JvmStatic
    fun foobarOutputRelay(): Relay<FooBar.Output> =
        PublishRelay.create()

    @RecyclerScope
    @Provides
    @JvmStatic
    fun helloWorldInput(relay: Relay<HelloWorld.Input>): ObservableSource<HelloWorld.Input> =
        relay

    @RecyclerScope
    @Provides
    @JvmStatic
    fun helloWorldInputRelay(): Relay<HelloWorld.Input> =
        PublishRelay.create()

    @RecyclerScope
    @Provides
    @JvmStatic
    fun helloWorldOutput(relay: Relay<HelloWorld.Output>): Consumer<HelloWorld.Output> =
        relay

    @RecyclerScope
    @Provides
    @JvmStatic
    fun helloWorldOutputRelay(): Relay<HelloWorld.Output> =
        PublishRelay.create()

    @RecyclerScope
    @Provides
    @JvmStatic
    fun tabs(): Tabs =
        Tabs()

}

internal data class Tabs(
    val list: List<RecyclerItem> = listOf(
        RecyclerItem.FooBar,
        RecyclerItem.HelloWorld,
        RecyclerItem.DialogExample
    )
)