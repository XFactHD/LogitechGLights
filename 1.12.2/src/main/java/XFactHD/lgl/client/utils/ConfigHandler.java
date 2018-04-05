/*  Copyright (C) <2018>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.lgl.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.HashMap;

public class ConfigHandler
{
    public static final ConfigHandler INSTANCE = new ConfigHandler();
    private static Configuration configuration;

    private HashMap<String, Integer> keyCatColorMap = new HashMap<>();

    public static int getColorForKeyCategory(String category)
    {
        System.out.println(category);
        System.out.println(INSTANCE.keyCatColorMap);
        return INSTANCE.keyCatColorMap.getOrDefault(category, INSTANCE.keyCatColorMap.get("key.categories.unknown"));
    }

    public static void setColorForKeyCategory(String category, int color)
    {
        INSTANCE.keyCatColorMap.replace(category, color);
        Property prop = configuration.getCategory("key_colors").get(category);
        if (prop != null) { prop.set(color + 0xFFFFFF + 1); } //FIXME: stupid workaround for a dumb bug (color goes negative)
        if (configuration.hasChanged()) { configuration.save(); }
    }

    public void init(File configFile)
    {
        if (configuration == null)
        {
            configuration = new Configuration(configFile);
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equalsIgnoreCase(Reference.MODID))
        {
            loadConfiguration();
        }
    }

    public void loadConfiguration()
    {
        keyCatColorMap.put("key.categories.unknown", configuration.getInt("key.categories.unknown", "key_colors", 0xFF0000, 0x000000, 0xFFFFFF, "Key color in hex for keys with a category unknown to the system"));
        keyCatColorMap.put("key.categories.inventory.selected", configuration.getInt("key.categories.inventory.selected", "key_colors", 0xFF7F00, 0x000000, 0xFFFFFF, "Key color in hex for the selected inv slot key"));
        keyCatColorMap.put("key.categories.dead", configuration.getInt("key.categories.dead", "key_colors", 0xFF0000, 0x000000, 0xFFFFFF, "Key color in hex when you are dead"));

        keyCatColorMap.put("key.categories.movement",    configuration.getInt("key.categories.movement",    "key_colors", 0x00DCFF, 0x000000, 0xFFFFFF, "Key color in hex for category 'key.categories.movement'"));
        keyCatColorMap.put("key.categories.gameplay",    configuration.getInt("key.categories.gameplay",    "key_colors", 0xFFFFFF, 0x000000, 0xFFFFFF, "Key color in hex for category 'key.categories.gameplay'"));
        keyCatColorMap.put("key.categories.inventory",   configuration.getInt("key.categories.inventory",   "key_colors", 0x00FF00, 0x000000, 0xFFFFFF, "Key color in hex for category 'key.categories.inventory'"));
        keyCatColorMap.put("key.categories.creative",    configuration.getInt("key.categories.creative",    "key_colors", 0x8000FF, 0x000000, 0xFFFFFF, "Key color in hex for category 'key.categories.creative'"));
        keyCatColorMap.put("key.categories.multiplayer", configuration.getInt("key.categories.multiplayer", "key_colors", 0xFFDC00, 0x000000, 0xFFFFFF, "Key color in hex for category 'key.categories.multiplayer'"));
        keyCatColorMap.put("key.categories.ui",          configuration.getInt("key.categories.ui",          "key_colors", 0x0000FF, 0x000000, 0xFFFFFF, "Key color in hex for category 'key.categories.ui'"));
        keyCatColorMap.put("key.categories.misc",        configuration.getInt("key.categories.misc",        "key_colors", 0x0000FF, 0x000000, 0xFFFFFF, "Key color in hex for category 'key.categories.misc'"));

        for (KeyBinding binding : Minecraft.getMinecraft().gameSettings.keyBindings)
        {
            if (!keyCatColorMap.containsKey(binding.getKeyCategory()))
            {
                int value = configuration.getInt(binding.getKeyCategory(), "key_colors", 0x00FF00, 0x000000, 0xFFFFFF, "Key color in hex for category '" + binding.getKeyCategory() + "'");
                keyCatColorMap.put(binding.getKeyCategory(), value);
            }
        }

        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }

    public HashMap<String, Integer> getCategoryMap()
    {
        return keyCatColorMap;
    }
}