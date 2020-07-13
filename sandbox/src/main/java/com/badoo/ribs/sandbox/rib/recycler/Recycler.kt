package com.badoo.ribs.sandbox.rib.recycler

import com.badoo.ribs.android.activitystarter.CanProvideActivityStarter
import com.badoo.ribs.android.dialog.CanProvideDialogLauncher
import com.badoo.ribs.android.permissionrequester.CanProvidePermissionRequester
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.portal.CanProvidePortal

interface Recycler : Rib {

    interface Dependency :
        CanProvidePermissionRequester,
        CanProvideActivityStarter,
        CanProvidePortal,
        CanProvideDialogLauncher

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: RecyclerView.Factory = RecyclerViewImpl.Factory()
    ) : RibCustomisation
}
