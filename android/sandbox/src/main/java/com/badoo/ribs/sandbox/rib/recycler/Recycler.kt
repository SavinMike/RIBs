package com.badoo.ribs.sandbox.rib.recycler

import com.badoo.ribs.android.CanProvideActivityStarter
import com.badoo.ribs.android.CanProvidePermissionRequester
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.routing.transition.handler.TransitionHandler
import com.badoo.ribs.customisation.CanProvidePortal
import com.badoo.ribs.customisation.RibCustomisation
import com.badoo.ribs.dialog.CanProvideDialogLauncher
import com.badoo.ribs.dialog.DialogLauncher

interface Recycler : Rib {

    interface Dependency :
        CanProvidePermissionRequester,
        CanProvideActivityStarter,
        CanProvidePortal,
        CanProvideDialogLauncher

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: RecyclerView.Factory = RecyclerViewImpl.Factory(),
        val transitionHandler: TransitionHandler<RecyclerRouter.Configuration>? = null
    ) : RibCustomisation
}
