package com.crumbed.crumbmmo.ecs.components;

import com.crumbed.crumbmmo.ecs.EntityComponent;
import com.google.gson.annotations.SerializedName;

public class NpcComponent extends EntityComponent {
    public static int ID;
    @Override
    public int id() { return ID; }

    @SerializedName("look-at-player")
    public boolean lookAtPlayer;
    @SerializedName("skin-signature")
    public String skinSignature;
    @SerializedName("skin-texture")
    public String skinTexture;

    public NpcComponent(boolean lookAtPlayer) {
        this.lookAtPlayer = lookAtPlayer;
    }
}











