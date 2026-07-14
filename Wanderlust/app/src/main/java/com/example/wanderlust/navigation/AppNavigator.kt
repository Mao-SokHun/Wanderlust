package com.example.wanderlust.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.wanderlust.ui.components.WanderlustNavTab

/**
 * Back stack for [com.example.wanderlust.MainActivity].
 * Uses [mutableStateOf] so Compose + [androidx.activity.compose.BackHandler] recompose on push/pop.
 */
class AppNavigator(
    initial: AppScreen = AppScreen.Splash,
) {
    var stack by mutableStateOf(listOf(initial))
        private set

    val current: AppScreen get() = stack.last()

    val canPop: Boolean get() = stack.size > 1

    fun push(screen: AppScreen) {
        stack = stack + screen
    }

    fun pop(): Boolean {
        if (stack.size <= 1) return false
        stack = stack.dropLast(1)
        return true
    }

    fun resetTo(screen: AppScreen) {
        stack = listOf(screen)
    }

    fun popToMain(tab: WanderlustNavTab) {
        var s = stack
        while (s.size > 1 && s.last() !is AppScreen.Main) {
            s = s.dropLast(1)
        }
        stack = if (s.last() is AppScreen.Main) {
            s.dropLast(1) + AppScreen.Main(tab)
        } else {
            listOf(AppScreen.Main(tab))
        }
    }

    fun switchMainTab(tab: WanderlustNavTab) {
        stack = if (stack.last() is AppScreen.Main) {
            stack.dropLast(1) + AppScreen.Main(tab)
        } else {
            var s = stack
            while (s.size > 1 && s.last() !is AppScreen.Main) {
                s = s.dropLast(1)
            }
            if (s.last() is AppScreen.Main) {
                s.dropLast(1) + AppScreen.Main(tab)
            } else {
                listOf(AppScreen.Main(tab))
            }
        }
    }

    fun mainTabOrDefault(default: WanderlustNavTab = WanderlustNavTab.Home): WanderlustNavTab =
        (current as? AppScreen.Main)?.tab
            ?: (stack.lastOrNull { it is AppScreen.Main } as? AppScreen.Main)?.tab
            ?: default
}
