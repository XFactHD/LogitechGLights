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

package XFactHD.lgl.client;

import XFactHD.lgl.LogitechGLights;
import XFactHD.lgl.client.gui.GuiColorConfig;
import XFactHD.lgl.client.utils.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy
{
    public void preInit()
    {
        LogitechGLights.handler = LightHandler.init();
        MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);
    }

    public void init()
    {
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(LogitechGLights.handler);
        EventHandler.INSTANCE.init();
        GuiColorConfig.init();
    }

    public void postInit()
    {
        ConfigHandler.INSTANCE.loadConfiguration();
        EventHandler.populateInvSlotKeys();
        LogitechGLights.handler.initBaseLighting();
    }
}