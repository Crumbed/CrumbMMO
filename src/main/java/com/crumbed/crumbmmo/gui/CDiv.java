package com.crumbed.crumbmmo.gui;

/*
import com.crumbed.crumbmmo.items.CItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.function.Function;

public class CDiv {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    public static final int BOTTOM = 3;

    public byte x = 0, y = 0;
    public byte w, h;
    public ItemStack backgroundItem;
    public ArrayList<CDiv> body;
    public ArrayList<Function<InventoryClickEvent, Void>> listeners;
    public int[] padding = null;
    public int[] margin = null;
    public Align align = Align.Left;


    public static CDiv div() {
        return new CDiv((byte) 0, (byte) 0);
    }
    private CDiv(byte width, byte height) {
        w = width;
        h = height;
        backgroundItem = new ItemStack(Material.AIR);
        body = new ArrayList<>();
    }
    public CDiv() {
        w = 0;
        h = 0;
        backgroundItem = new ItemStack(Material.AIR);
        body = new ArrayList<>();
    }

    public CDiv addBody(CDiv div) {
        if (bitMask(div.w, div.h) == 0) {
            div.w = w;
            div.h = h;
        }


        body.add(div);
        return this;
    }
    public CDiv bg(ItemStack item) {
        backgroundItem = item;
        return this;
    }
    public CDiv pos(byte x, byte y) {
        this.x = x;
        this.y = y;
        return this;
    }
    public CDiv size(byte width, byte height) {
        this.w = width;
        this.h = height;
        return this;
    }
    public CDiv addListener(Function<InventoryClickEvent, Void> listener) {
        listeners.add(listener);
        return this;
    }
    public CDiv margin(
        int left,
        int right,
        int top,
        int bottom
    ) {
        margin = new int[] { left, right, top, bottom };
        return this;
    }
    public CDiv padding(
        int left,
        int right,
        int top,
        int bottom
    ) {
        padding = new int[] { left, right, top, bottom };
        return this;
    }
    public CDiv margin(int margin) {
        this.margin[0] = margin;
        this.margin[1] = margin;
        this.margin[2] = margin;
        this.margin[3] = margin;
        return this;
    }
    public CDiv padding(int padding) {
        this.padding[0] = padding;
        this.padding[1] = padding;
        this.padding[2] = padding;
        this.padding[3] = padding;
        return this;
    }
    public CDiv align(Align a) {
        align = a;
        return this;
    }

    public static short bitMask(byte a, byte b) {
        return (short) (((short) b >> 8) + a);
    }
    public int[] calcBodyPtr(int bodyW, int bodyH) {
        return switch (align) {
            case Center -> {
                var remW = w - bodyW;
                var remH = h - bodyH;
                yield new int[] {
                    remW / 2 - 1,
                    remH / 2 - 1
                };
            }
            case Left -> new int[] {
                x + padding[0],
                y + padding[2]
            };
            case Right -> new int[] {
                w - padding[1] - 1,
                h - padding[2] - 1
            };
        };
    }

    public enum Align {
        Center,
        Left,
        Right,
    }
}






























*/