package com.crumbed.crumbmmo.serializable;

import com.crumbed.crumbmmo.commands.CustomCommand;
import com.crumbed.crumbmmo.items.ItemComponent;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import static org.reflections.scanners.Scanners.SubTypes;

public class ComponentAdapter<T> implements JsonDeserializer<T> {

    @Override
    public T deserialize(JsonElement elem, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        final var member = (JsonObject) elem;
        var gson = new GsonBuilder()
                .registerTypeAdapter(ItemComponent.class, new ComponentAdapter<T>())
                .create();


        Reflections classes = new Reflections("com.crumbed.crumbmmo.items.components");
        for (Class<?> clazz : classes.get(SubTypes.of(ItemComponent.class).asClass())) {
            var fields = clazz.getDeclaredFields();
            var match = true;

            for (var f : fields) {
                var name = switch (f.getAnnotation(SerializedName.class)) {
                    case null -> f.getName();
                    case SerializedName a -> a.value();
                };
                if (name.equals("ID")) continue;
                Bukkit.getLogger().info(name);

                if (!member.has(name)) {
                    match = false;
                    break;
                }

                var memObj = member.get(name);
                try {
                    var serMem = gson.fromJson(memObj, f.getType());
                    if (serMem == null) {
                        match = false;
                        break;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    match = false;
                    break;
                }
            }

            if (!match) continue;
            return gson.fromJson(member, (Type) clazz);
        }

        throw new JsonParseException("Failed to parse ItemComponent!");
    }
}















