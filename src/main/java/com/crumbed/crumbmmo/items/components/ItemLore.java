package com.crumbed.crumbmmo.items.components;

import com.crumbed.crumbmmo.items.ItemComponent;

import java.util.ArrayList;

public class ItemLore extends ItemComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    public ArrayList<String> lore;

    public ItemLore() { lore = new ArrayList<>(); }
    public ItemLore(ArrayList<String> lore) { this.lore = lore; }

    @Override
    public ArrayList<String> toLore() { return lore; }
}
