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
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class EventHandler
{
    private static final int[] INV_SLOT_KEY_CODES = new int[9];
    public static final EventHandler INSTANCE = new EventHandler();

    private KeyBinding openConfig = new KeyBinding("key.lgl.open_config", Keyboard.KEY_C, "key.categories.ui");
    private boolean focused = true;
    private boolean dead = false;
    private boolean hasRespawned = false;
    private int lastSlot = -1;

    public void init()
    {
        ClientRegistry.registerKeyBinding(openConfig);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityPlayer && entity.equals(Minecraft.getMinecraft().player) && !event.isCanceled())
        {
            dead = true;
            LogitechGLights.handler.saveCurrentLighting();
            LogitechGLights.handler.setSolidColor(ConfigHandler.getColorForKeyCategory("key.categories.dead"));
        }
    }

    @SubscribeEvent
    public void onRespawnButton(GuiScreenEvent.ActionPerformedEvent event)
    {
        if (event.getGui() instanceof GuiGameOver)
        {
            if (event.getButton().id == 0)
            {
                hasRespawned = true;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) { return; }

        if (dead && hasRespawned)
        {
            dead = false;
            hasRespawned = false;
            LogitechGLights.handler.restoreLastLighting();
        }

        if (!dead && lastSlot != player.inventory.currentItem)
        {
            if (lastSlot != -1) { LogitechGLights.handler.setSolidColorOnKey(INV_SLOT_KEY_CODES[lastSlot], ConfigHandler.getColorForKeyCategory("key.categories.inventory")); }
            lastSlot = player.inventory.currentItem;
            LogitechGLights.handler.setSolidColorOnKey(INV_SLOT_KEY_CODES[lastSlot], ConfigHandler.getColorForKeyCategory("key.categories.inventory.selected"));
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (Display.isCreated())
            {
                if (focused && !Display.isActive())
                {
                    focused = false;
                    LogitechGLights.handler.shutdown(true);
                }
                else if (!focused && Display.isActive())
                {
                    focused = true;
                    LogitechGLights.handler.restart(true);
                    if (dead && !hasRespawned)
                    {
                        LogitechGLights.handler.setSolidColor(ConfigHandler.getColorForKeyCategory("key.categories.dead"));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        if (dead)
        {
            dead = false;
            LogitechGLights.handler.restoreLastLighting();
        }

        if (lastSlot != -1)
        {
            LogitechGLights.handler.setSolidColorOnKey(INV_SLOT_KEY_CODES[lastSlot], ConfigHandler.getColorForKeyCategory("key.categories.inventory"));
            lastSlot = -1;
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (openConfig.isPressed())
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiColorConfig());
        }
    }

    public static void populateInvSlotKeys()
    {
        for (int i = 0; i < 9; i++)
        {
            INV_SLOT_KEY_CODES[i] = Minecraft.getMinecraft().gameSettings.keyBindsHotbar[i].getKeyCode();
        }
    }

    public void reinit()
    {
        lastSlot = -1;
    }
}