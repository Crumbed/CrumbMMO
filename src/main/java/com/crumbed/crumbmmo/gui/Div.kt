package com.crumbed.crumbmmo.gui

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.LinkedList


enum class Align {
    Left,
    Right,
    Center
}

class Vec2(x: Int = 1, y: Int = 1) {
    var x = x
    var y = y

    fun setXY(x: Int = 1, y: Int = 1) {
        this.x = x
        this.y = y
    }
}

abstract class Component {
    var pos = Vec2()
    var size = Vec2()
    var background = ItemStack(Material.AIR)

    fun pos(x: Int, y: Int) {
        if (x < 0 || x > 8) {
            Bukkit.getLogger().info("${ChatColor.RED}GUI Error: X position must be within 0-8, but value given was $x")
        } else if (y < 0 || y > 5) {
            Bukkit.getLogger().info("${ChatColor.RED}GUI Error: Y position must be within 0-5, but value given was $y")
        }

        pos.setXY(x, y)
    }
    fun size(x: Int, y: Int) {
        if (x < 1 || x > 9) {
            Bukkit.getLogger().info("${ChatColor.RED}GUI Error: Width must be within 1-9, but value given was $x")
        } else if (y < 1 || y > 6) {
            Bukkit.getLogger().info("${ChatColor.RED}GUI Error: Height must be within 1-6, but value given was $y")
        }

        size.setXY(x, y)
    }
}

class Div : Component() {
    var id: String = ""
    var body = LinkedList<Component>()
    var align = Align.Left


    fun alignItems(align: Align) { this.align = align }

    fun div(id: String, init: Div.() -> Unit): Div {
        val div = Div()
        div.id = id
        if (body.isEmpty()) {
            div.pos = pos
            div.init()
            return div
        }

        div.pos = calculatePos(div)


        div.init()

    }

    private fun calculatePos(cmp: Component) : Vec2 {
        val last = body.last
        // dont subtract 1 because the next element cant overlap
        var pos = Vec2(last.pos.x + last.size.x)
        if (last.pos.x + last.size.x + cmp.size.x - 1 > size.x) {
            var height = 1
            for (c in body) {
                if (c.size.y > height) {
                    height = c.size.y
                }

                if (c == last) break
            }

            pos.setXY(0, height)
        }

        return pos
    }
}

class Button : Component() {
    var id: String = ""
}































