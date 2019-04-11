package com.badoo.ribs.example.rib.switcher

import com.badoo.ribs.core.Builder
import com.badoo.ribs.core.Node
import com.badoo.ribs.dialog.DialogLauncher
import com.badoo.ribs.example.rib.blocker.builder.BlockerBuilder
import com.badoo.ribs.example.rib.dialog_example.builder.DialogExampleBuilder
import com.badoo.ribs.example.rib.foo_bar.builder.FooBarBuilder
import com.badoo.ribs.example.rib.hello_world.builder.HelloWorldBuilder
import com.badoo.ribs.example.rib.menu.Menu
import com.badoo.ribs.example.rib.menu.builder.MenuBuilder
import com.badoo.ribs.example.rib.switcher.dialog.DialogToTestOverlay
import com.badoo.ribs.example.rib.util.TestNode
import com.badoo.ribs.example.rib.util.subscribeOnTestObserver
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SwitcherRouterTest {

    private val fooBarBuilder = createBuilder<FooBarBuilder> { build() }
    private val fooBarNode = fooBarBuilder.build()

    private val helloWorldBuilder = createBuilder<HelloWorldBuilder> { build() }
    private val helloWorldNode = helloWorldBuilder.build()

    private val dialogExampleBuilder = createBuilder<DialogExampleBuilder> { build() }
    private val dialogExampleNode = dialogExampleBuilder.build()

    private val blockerBuilder = createBuilder<BlockerBuilder> { build() }
    private val blockerNode = blockerBuilder.build()

    private val menuBuilder = createBuilder<MenuBuilder> { build() }
    private val menuNode = menuBuilder.build()

    private val dialogLauncher: DialogLauncher = mock()
    private val dialogToTestOverlay: DialogToTestOverlay = mock()

    private val router = SwitcherRouter(
        fooBarBuilder,
        helloWorldBuilder,
        dialogExampleBuilder,
        blockerBuilder,
        menuBuilder,
        dialogLauncher,
        dialogToTestOverlay
    )

    private val currentNode = TestNode(router)

    @Test
    fun `attach - attaches menu and dialog example node`() {
        router.onAttach(null)

        assertThat(currentNode.getChildren()).containsExactlyInAnyOrder(menuNode, dialogExampleNode)
    }

    @Test
    fun `attach - publishes select dialog menu item event`() {
        val observer = router.menuUpdater.subscribeOnTestObserver()

        router.onAttach(null)

        observer.assertValue(Menu.Input.SelectMenuItem(Menu.MenuItem.Dialogs))
    }

    @Test
    fun `hello configuration - attaches hello world and menu nodes`() {
        router.onAttach(null)

        router.replace(SwitcherRouter.Configuration.Hello)

        assertThat(currentNode.getChildren()).containsExactlyInAnyOrder(menuNode, helloWorldNode)
    }

    @Test
    fun `hello configuration - publishes select hello world menu item event`() {
        router.onAttach(null)
        val observer = router.menuUpdater.subscribeOnTestObserver()

        router.replace(SwitcherRouter.Configuration.Hello)

        observer.assertValue(Menu.Input.SelectMenuItem(Menu.MenuItem.HelloWorld))
    }

    @Test
    fun `foo configuration - attaches foo bar and menu nodes`() {
        router.onAttach(null)

        router.replace(SwitcherRouter.Configuration.Foo)

        assertThat(currentNode.getChildren()).containsExactlyInAnyOrder(menuNode, fooBarNode)
    }

    @Test
    fun `foo configuration - publishes select foo bar menu item event`() {
        router.onAttach(null)
        val observer = router.menuUpdater.subscribeOnTestObserver()

        router.replace(SwitcherRouter.Configuration.Foo)

        observer.assertValue(Menu.Input.SelectMenuItem(Menu.MenuItem.FooBar))
    }

    @Test
    fun `overlay dialog configuration - shows overlay dialog`() {
        router.onAttach(null)

        router.replace(SwitcherRouter.Configuration.OverlayDialog)

        verify(dialogLauncher).show(dialogToTestOverlay)
    }

    @Test
    fun `blocker configuration - attaches blocker and menu nodes`() {
        router.onAttach(null)

        router.replace(SwitcherRouter.Configuration.Blocker)

        assertThat(currentNode.getChildren()).containsExactlyInAnyOrder(menuNode, blockerNode)
    }

    private inline fun <reified B : Builder<*>> createBuilder(noinline buildCall: B.() -> Node<*>) =
        mock<B> {
            on(buildCall) doReturn mock(name = "Node mock for ${B::class.java.simpleName}")
        }
}
