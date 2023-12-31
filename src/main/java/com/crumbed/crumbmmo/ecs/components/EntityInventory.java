package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.crumbed.crumbmmo.items.CItem;

import java.util.ArrayList;

public class EntityInventory extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }
    public CItem armor[]; // 5 long
    public transient CItem inventory[]; // 36 long
    public int activeSlot;
    public transient ArrayList<CItem> statBoosts;
    public transient boolean hasUpdated;

    public EntityInventory() {
        armor = new CItem[5];
        inventory = new CItem[36];
        activeSlot = 0;
        statBoosts = new ArrayList<>();
        hasUpdated = true;
    }
    public EntityInventory(int inventorySize, int heldItem) {
        armor = new CItem[5];
        inventory = new CItem[inventorySize];
        activeSlot = heldItem;
        statBoosts = new ArrayList<>();
        hasUpdated = true;
    }
}




























