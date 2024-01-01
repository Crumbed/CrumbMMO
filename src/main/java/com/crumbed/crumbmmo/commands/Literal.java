package com.crumbed.crumbmmo.commands;

public record Literal(String... lit) implements TabComponent.Source {
    @Override
    public String[] getTabSource() { return lit.clone(); }
}
