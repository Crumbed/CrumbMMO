package com.crumbed.crumbmmo.items;

import java.util.ArrayList;

public abstract class ItemComponent {
    public static int ID;

    public abstract int id();

    public abstract ArrayList<String> toLore();
}
