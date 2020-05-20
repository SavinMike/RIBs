package com.badoo.ribs.sandbox.rib.recycler.recycler

import com.badoo.ribs.android.recyclerview.client.RecyclerViewRibResolver
import com.badoo.ribs.core.routing.action.AttachRibRoutingAction.Companion.attach
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.sandbox.rib.dialog_example.builder.DialogExampleBuilder
import com.badoo.ribs.sandbox.rib.foo_bar.FooBarBuilder
import com.badoo.ribs.sandbox.rib.hello_world.HelloWorldBuilder
import com.badoo.ribs.sandbox.rib.recycler.recycler.model.RecyclerItem

class RecyclerResolver(
    private val helloWorldBuilder: HelloWorldBuilder,
    private val fooBarBuilder: FooBarBuilder,
    private val dialogExampleBuilder: DialogExampleBuilder
) : RecyclerViewRibResolver<RecyclerItem> {

    override fun resolve(element: RecyclerItem): RoutingAction =
        when (element) {
            is RecyclerItem.HelloWorld -> attach { helloWorldBuilder.build(it) }
            is RecyclerItem.FooBar -> attach { fooBarBuilder.build(it) }
            is RecyclerItem.DialogExample -> attach { dialogExampleBuilder.build(it) }
        }
}