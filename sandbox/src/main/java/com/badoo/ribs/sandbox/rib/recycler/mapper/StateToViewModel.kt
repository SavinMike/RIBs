package com.badoo.ribs.sandbox.rib.recycler.mapper

import com.badoo.ribs.sandbox.rib.recycler.RecyclerView.ViewModel
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature.State
import com.badoo.ribs.sandbox.rib.recycler.recycler.model.RecyclerItem

internal object StateToViewModel : (State) -> ViewModel {

    override fun invoke(state: State): ViewModel =
        ViewModel(
            state.tabs.map {
                when(it){
                    is RecyclerItem.HelloWorld -> "HelloWorld"
                    is RecyclerItem.FooBar -> "FooBar"
                    is RecyclerItem.DialogExample -> "Dialog"
                }
            }
        )

}
