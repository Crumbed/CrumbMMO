package com.crumbed.crumbmmo.gui;

/*
import com.crumbed.crumbmmo.ecs.CPlayer;
import com.crumbed.crumbmmo.managers.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Stack;

public class GUI {
    public final String title;
    public final byte rows;
    public CDiv body;


    public GUI(String title, byte rows) {
        this.title = title;
        this.rows = rows;
        body = new CDiv().size((byte) 9, rows);
    }

    public Inventory createGui(Player p) {
        var gui = Bukkit.createInventory(p, rows, title);
        displayDivs(gui, body);

        return gui;
    }

    private void displayDivs(Inventory gui, CDiv div) {
        var bodyW = 0;
        var bodyH = 0;
        var rowW = 0;
        var rowH = 0;

        for (var x : div.body) {
            final var elementW = x.w + x.margin[0] + x.margin[1];
            final var elementH = x.h + x.margin[2] + x.margin[3];

            if (elementH > rowH) {
                bodyH += elementH - rowH;
                rowH = elementH;
            }
            if (rowW + elementW >= div.w - div.padding[1]) {
                bodyH += rowH;
                rowW = elementW;
                rowH = elementH;
            } else {
                rowW += elementW;
            }
            if (rowW > bodyW) {
                bodyW = rowW;
            }
        }

        var ptr = div.calcBodyPtr(bodyW, bodyH);
        if (div.backgroundItem.getType() != Material.AIR) {
            fillBackground(gui, div, ptr.clone(), bodyW, bodyH);
        }

        rowH = 0;
        for (var d : div.body) {
            final var dW = d.margin[0] + d.w + d.margin[1];
            final var dH = d.margin[2] + d.h + d.margin[3];

            // set x & y pos of each body div, dont forget padding and margin, div pos should NOT include the margin and
            // should be the start of where itemstacks go
            if (ptr[0] + dW >= bodyW - 1) {
                ptr[0] = ;
                ptr[1] = rowH - 1;
                rowH = dW;
            }
            if (rowH < dH) {
                rowH = dH;
            }



            displayDivs(gui, d);
        }
    }




    private void fillBackground(Inventory gui, CDiv div, int[] ptr, int bodyW, int bodyH) {
        for (; ptr[1] < bodyH; ptr[1]++) {
            for (; ptr[0] < bodyW; ptr[0]++) {
                final var i = index(ptr[0], ptr[1]);
                gui.setItem(i, div.backgroundItem);
            }
        }
    }


    public int index(int x, int y) { return y * 9 + x; }
}


























 */