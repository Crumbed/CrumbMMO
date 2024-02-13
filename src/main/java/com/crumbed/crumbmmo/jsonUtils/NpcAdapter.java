package com.crumbed.crumbmmo.jsonUtils;

import com.crumbed.crumbmmo.ecs.components.NpcComponent;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class NpcAdapter implements JsonSerializer<NpcData>, JsonDeserializer<NpcData> {
    @Override
    public NpcData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        final var obj = (JsonObject) element;
        final var id = obj.get("id").getAsString();
        final var name = obj.get("name").getAsString();
        final var flags = (NpcComponent) context.deserialize(obj.get("flags"), NpcComponent.class);
        final var jsonLoc = obj.getAsJsonObject("loc");
        final var pos = jsonLoc.getAsJsonArray("pos");
        final var loc = new Location(
                Bukkit.getWorld(jsonLoc.get("world").getAsString()),
                pos.get(0).getAsDouble(),
                pos.get(1).getAsDouble(),
                pos.get(2).getAsDouble()
        );

        return new NpcData(
                id,
                name,
                loc,
                flags
        );
    }

    @Override
    public JsonElement serialize(NpcData data, Type type, JsonSerializationContext context) {
        var element = new JsonObject();
        element.add("id", context.serialize(data.id));
        element.add("name", context.serialize(data.name));
        element.add("flags", context.serialize(data.flags));

        var loc = new JsonObject();
        loc.add("world", context.serialize(data.loc.getWorld().getName()));
        loc.add("pos", context.serialize(new double[] {
                data.loc.getX(), data.loc.getY(), data.loc.getZ()
        }));
        element.add("loc", loc);

        return element;
    }
}
