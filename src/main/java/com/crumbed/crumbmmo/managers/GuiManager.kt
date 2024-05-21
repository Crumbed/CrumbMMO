package com.crumbed.crumbmmo.managers

import com.crumbed.crumbmmo.gui.Component
import com.crumbed.crumbmmo.gui.Div
import org.bukkit.inventory.Inventory

class GuiManager {
    companion object {
        lateinit var instance: GuiManager
    }
    init {
        instance = this
    }

    fun render(inv: Inventory, ui: Component) {
        when (ui) {
            is Div -> {

            }
        }
    }
}
