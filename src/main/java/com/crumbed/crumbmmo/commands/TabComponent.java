package com.crumbed.crumbmmo.commands;

import com.crumbed.crumbmmo.utils.Option;

public record TabComponent(Type type, Option<? extends Source> tabSource, boolean optional) {

    public enum Type {
        PlayerName,
        Id,
        Count,
        Int,
        Literal
    }

    public interface Source {
        String[] getTabSource();
    }

    public record Literal(String lit) implements Source {
        @Override
        public String[] getTabSource() {
            return new String[] { lit };
        }
    }
}















