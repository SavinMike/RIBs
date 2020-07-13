package com.badoo.ribs.sandbox.rib.recycler.feature

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature.Effect
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature.News
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature.State
import com.badoo.ribs.sandbox.rib.recycler.feature.RecyclerFeature.Wish
import com.badoo.ribs.sandbox.rib.recycler.recycler.model.RecyclerItem
import io.reactivex.Observable
import io.reactivex.Observable.empty

internal class RecyclerFeature(
    tabs: List<RecyclerItem>
) : ActorReducerFeature<Wish, Effect, State, News>(
    initialState = State(tabs),
    bootstrapper = BootStrapperImpl(),
    actor = ActorImpl(),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    data class State(
        val tabs: List<RecyclerItem>
    )

    sealed class Wish

    sealed class Effect

    sealed class News

    class BootStrapperImpl : Bootstrapper<Wish> {
        override fun invoke(): Observable<Wish> =
            empty()
    }

    class ActorImpl : Actor<State, Wish, Effect> {
        override fun invoke(state: State, wish: Wish): Observable<Effect> =
            empty()
    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State =
            state
    }

    class NewsPublisherImpl : NewsPublisher<Wish, Effect, State, News> {
        override fun invoke(wish: Wish, effect: Effect, state: State): News? =
            null
    }
}
