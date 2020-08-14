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

package XFactHD.lgl;

import XFactHD.lgl.client.ClientProxy;
import XFactHD.lgl.client.LightHandler;
import XFactHD.lgl.client.utils.ConfigHandler;
import XFactHD.lgl.client.utils.LogHelper;
import XFactHD.lgl.client.utils.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, clientSideOnly = true, canBeDeactivated = true)
public class LogitechGLights
{
    @Mod.Instance
    public static LogitechGLights INSTANCE;

    public static LightHandler handler;

    private ClientProxy proxy = new ClientProxy();

    static { Runtime.getRuntime().addShutdownHook(new ShutdownThread()); }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LogHelper.setLogger(event.getModLog());

        LogHelper.info("Hello Minecraft!");
        LogHelper.info("Starting PreInit!");
        proxy.preInit();
        MinecraftForge.EVENT_BUS.register(ConfigHandler.INSTANCE);
        ConfigHandler.INSTANCE.init(event.getSuggestedConfigurationFile());
        LogHelper.info("Finished PreInit!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        LogHelper.info("Starting Init!");
        proxy.init();
        LogHelper.info("Finished Init!");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        LogHelper.info("Starting PostInit!");
        proxy.postInit();
        LogHelper.info("Finished PostInit!");
    }

    @Mod.EventHandler
    public void disable(FMLModDisabledEvent event)
    {
        //if (event.getModId().equals(Reference.MODID))
        {
            LogHelper.info("Disabling LogitechGLights!");
            handler.shutdown(false);
        }
    }

    private static class ShutdownThread extends Thread
    {
        @Override
        public void run()
        {
            handler.shutdown(false);
        }
    }
}