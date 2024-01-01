package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;

public class NpcComponent extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    public boolean lookAtPlayer;
    public String skinSignature;
    public String skinTexture;

    public NpcComponent(boolean lookAtPlayer) {
        this.lookAtPlayer = lookAtPlayer;
    }
}











