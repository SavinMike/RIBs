/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.badoo.ribs.sandbox.rib.recycler.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.lang.ref.WeakReference
import kotlin.math.abs

class TabLayoutRecyclerMediator(
    private val tabLayout: TabLayout,
    private val recyclerView: RecyclerView,
    private val autoRefresh: Boolean = true,
    private val recyclerViewItemResolver: RecyclerViewItemResolver,
    private val tabConfigurationStrategy: (tab: TabLayout.Tab, position: Int) -> Unit
) {
    private var adapter: RecyclerView.Adapter<*>? = null
    private var attached = false
    private var onPageChangeCallback: TabLayoutOnPageChangeCallback? = null
    private var pagerAdapterObserver: RecyclerView.AdapterDataObserver? = null


    fun attach() {
        check(!attached) { "TabLayoutMediator is already attached" }
        adapter = recyclerView.adapter
        checkNotNull(adapter) { "TabLayoutMediator attached before RecyclerView has an " + "adapter" }
        attached = true
        onPageChangeCallback = TabLayoutOnPageChangeCallback(tabLayout, recyclerView, recyclerViewItemResolver).also {
            recyclerView.addOnScrollListener(it)

            tabLayout.addOnTabSelectedListener(it)
        }

        // Now we'll populate ourselves from the pager adapter, adding an observer if
        // autoRefresh is enabled
        if (autoRefresh) { // Register our observer on the new adapter
            pagerAdapterObserver = PagerAdapterObserver().also {
                adapter!!.registerAdapterDataObserver(it)
            }
        }
        populateTabsFromPagerAdapter()
        tabLayout.setScrollPosition(recyclerView.currentItem, 0f, true)
    }

    fun detach() {
        if (autoRefresh && adapter != null) {
            adapter!!.unregisterAdapterDataObserver(pagerAdapterObserver!!)
            pagerAdapterObserver = null
        }
        onPageChangeCallback?.let {
            tabLayout.removeOnTabSelectedListener(it)
            recyclerView.removeOnScrollListener(it)
        }
        onPageChangeCallback = null
        adapter = null
        attached = false
    }

    fun populateTabsFromPagerAdapter() {
        tabLayout.removeAllTabs()
        if (adapter != null) {
            val adapterCount = adapter!!.itemCount
            for (i in 0 until adapterCount) {
                val tab = tabLayout.newTab()
                tabConfigurationStrategy(tab, i)
                tabLayout.addTab(tab, false)
            }
            // Make sure we reflect the currently set RecyclerView item
            if (adapterCount > 0) {
                val lastItem = tabLayout.tabCount - 1
                val currItem = Math.min(recyclerView.currentItem, lastItem)
                if (currItem != tabLayout.selectedTabPosition) {
                    tabLayout.selectTab(tabLayout.getTabAt(currItem))
                }
            }
        }
    }

    private class TabLayoutOnPageChangeCallback internal constructor(
        tabLayout: TabLayout,
        recyclerView: RecyclerView,
        private val recyclerViewItemResolver: RecyclerViewItemResolver
    ) : RecyclerView.OnScrollListener(), OnTabSelectedListener {
        private val tabLayoutRef: WeakReference<TabLayout> = WeakReference(tabLayout)
        private val recyclerViewRef: WeakReference<RecyclerView> = WeakReference(recyclerView)
        private var previousScrollState: Int? = null
        private var scrollState = RecyclerView.SCROLL_STATE_IDLE
        private var totalDiff = 0f
        private var currentPosition = 0

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            previousScrollState = scrollState
            scrollState = newState

            if (newState == RecyclerView.SCROLL_STATE_IDLE && previousScrollState != RecyclerView.SCROLL_STATE_IDLE) {
                totalDiff = 0f
                onPageSelected(recyclerViewItemResolver.getCurrentItem(recyclerView), true)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            totalDiff += dx
            tabLayoutRef.get()?.let { tabLayout ->
                val updateText = scrollState != RecyclerView.SCROLL_STATE_SETTLING || previousScrollState == RecyclerView.SCROLL_STATE_DRAGGING

                val updateIndicator =
                    !(scrollState == RecyclerView.SCROLL_STATE_SETTLING &&
                        (previousScrollState == RecyclerView.SCROLL_STATE_IDLE || previousScrollState == null))

                val freq = abs(totalDiff / recyclerView.width.toFloat())

                val position = if (totalDiff < 0) currentPosition - 1 else currentPosition
                val offset = if (totalDiff < 0) 1 - freq else freq

                if (
                    scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                    (previousScrollState == RecyclerView.SCROLL_STATE_SETTLING || previousScrollState == null)
                ) {
                    if (tabLayout.selectedTabPosition == -1) {
                        tabLayout.selectTab(tabLayout.getTabAt(currentPosition), true)
                    }
                } else {
                    tabLayout.setScrollPosition(position, offset, updateText, updateIndicator)
                }
            }
        }

        private fun onPageSelected(position: Int, resetOffset: Boolean) {
            currentPosition = position
            val tabLayout = tabLayoutRef.get()
            if (tabLayout != null && tabLayout.selectedTabPosition != position && position < tabLayout.tabCount) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
                val updateIndicator = (scrollState == RecyclerView.SCROLL_STATE_IDLE
                    || (scrollState == RecyclerView.SCROLL_STATE_SETTLING
                    && (previousScrollState == RecyclerView.SCROLL_STATE_IDLE || previousScrollState == null)))
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator)
            } else {
                if (resetOffset) {
                    tabLayout?.setScrollPosition(tabLayout.selectedTabPosition, 0f, false)
                }
            }
        }

        fun reset() {
            scrollState = RecyclerView.SCROLL_STATE_IDLE
            previousScrollState = scrollState
            totalDiff = 0f
            currentPosition = 0
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            recyclerViewRef.get()?.let {
                recyclerViewItemResolver.setCurrentItem(it, tab.position, true)
            }
            onPageSelected(tab.position, false)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) { // No-op
        }

        override fun onTabReselected(tab: TabLayout.Tab) { // No-op
        }

    }

    private inner class PagerAdapterObserver internal constructor() : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }
    }

    interface RecyclerViewItemResolver {
        fun getCurrentItem(recyclerView: RecyclerView): Int

        fun setCurrentItem(recyclerView: RecyclerView, position: Int, withAnimation: Boolean)

    }

    private val RecyclerView.currentItem: Int
        get() = recyclerViewItemResolver.getCurrentItem(this)

    private fun RecyclerView.setCurrentItem(position: Int, withAnimation: Boolean) {
        recyclerViewItemResolver.setCurrentItem(this, position, withAnimation)
    }
}

class LinearLayoutManagerRecyclerViewItemResolver : TabLayoutRecyclerMediator.RecyclerViewItemResolver {
    override fun getCurrentItem(recyclerView: RecyclerView): Int =
        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

    override fun setCurrentItem(recyclerView: RecyclerView, position: Int, withAnimation: Boolean) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
    }

}
