package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.utils.Option;

public record TabComponent(Type type, Option<? extends Source> tabSource, boolean optional) {

    public enum Type {
        PlayerName,
        Id,
        Count,
        Number,
        Lit
    }

    public interface Source {
        String[] getTabSource();
    }


}















