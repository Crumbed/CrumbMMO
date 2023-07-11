package com.crumbed.crumbmmo.entity.components;

import com.crumbed.crumbmmo.entity.EntityComponent;
import com.crumbed.crumbmmo.utils.ActionBar;
import net.md_5.bungee.api.chat.TextComponent;
import org.w3c.dom.Text;

import javax.swing.*;
import java.util.Arrays;

public class EntityActionBar implements EntityComponent {

    private static final ComponentType TYPE = ComponentType.ActionBar;

    private ActionBar[] compononents;
    private ActionBar[] backup;

    private int ticksUntilRestore;

    public EntityActionBar(ActionBar space1, ActionBar space2, ActionBar space3){
        compononents = new ActionBar[3];
        backup = new ActionBar[3];
        compononents[0] = space1;
        compononents[1] = space2;
        compononents[2] = space3;
    }

    public void setSpace(int space, ActionBar ab){
        if (space > 2 || space < 0) return;
        compononents[space] = ab;
    }

    public void tempSetSpace(int space, ActionBar ab, int tickCount){
        if (space > 2 || space < 0) return;
        if (backup != null) backup = compononents.clone();
        ticksUntilRestore = tickCount;
        compononents[space] = ab;
    }

    public void restore(){
        if (ticksUntilRestore != 0 || backup == null) return;
        compononents = backup.clone();
        backup = null;
    }
    public TextComponent getActionBar(){
       return new TextComponent(compononents[0].genActBar() + "     " + compononents[1].genActBar() + "     " + compononents[2].genActBar());
    }

    @Override
    public ComponentType getType() { return TYPE; }

}