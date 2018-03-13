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

import XFactHD.lgl.client.utils.ConfigHandler;
import XFactHD.lgl.client.utils.LogHelper;
import com.logitech.gaming.LogiLED;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class LightHandler implements IResourceManagerReloadListener
{
    private final ArrayList<Method> restartCallbacks = new ArrayList<>();

    private HashMap<Integer, Integer> keyLastColorMap = new HashMap<>();
    private boolean active = true;

    //Sets all keys to a solid color in hex
    public void setSolidColor(int color)
    {
        if (!active) { return; }
        int[] colors = getColorsFromHex(color);
        LogiLED.LogiLedSetLighting(colors[0], colors[1], colors[2]);
    }

    //Sets all keys to a flashing solid color in hex with the specified duty cycle in milliseconds
    public void setFlashingColor(int color, int dutyCycle)
    {
        if (!active) { return; }
        int[] colors = getColorsFromHex(color);
        LogiLED.LogiLedFlashLighting(colors[0], colors[1], colors[2], LogiLED.LOGI_LED_DURATION_INFINITE, dutyCycle);
    }

    //Sets all keys to a pulsing solid color in hex with the specified duty cycle in milliseconds
    public void setPulsingColor(int color, int dutyCycle)
    {
        if (!active) { return; }
        int[] colors = getColorsFromHex(color);
        LogiLED.LogiLedPulseLighting(colors[0], colors[1], colors[2], LogiLED.LOGI_LED_DURATION_INFINITE, dutyCycle);
    }

    public void setSolidColorOnKey(int key, int color)
    {
        keyLastColorMap.put(key, color);
        int[] colors = getColorsFromHex(color);
        LogiLED.LogiLedSetLightingForKeyWithKeyName(key, colors[0], colors[1], colors[2]);
    }

    public void setFlashingColorOnKey(int key, int color, int dutyCycle)
    {
        int[] colors = getColorsFromHex(color);
        LogiLED.LogiLedFlashSingleKey(key, colors[0], colors[1], colors[2], LogiLED.LOGI_LED_DURATION_INFINITE, dutyCycle);
    }

    public void setPulsingColorOnKey(int key, int color, int dutyCycle)
    {
        int[] oldColors = getColorsFromHex(keyLastColorMap.getOrDefault(key, 0x000000));
        int[] newColors = getColorsFromHex(color);
        LogiLED.LogiLedPulseSingleKey(key, oldColors[0], oldColors[1], oldColors[2], newColors[0], newColors[1], newColors[2], dutyCycle, true);
    }

    public void stopEffects()
    {
        if (!active) { return; }
        LogiLED.LogiLedStopEffects();
    }

    public void saveCurrentLighting()
    {
        if (!active) { return; }
        LogiLED.LogiLedSaveCurrentLighting();
    }

    public void restoreLastLighting()
    {
        if (!active) { return; }
        stopEffects();
        onResourceManagerReload(null); //FIXME: remove this stupid workaround when the error is found
        //LogiLED.LogiLedRestoreLighting();
    }

    //TODO: check if there is a way to only color the mouse
    //public void setMouseColorSolid(int color)
    //{
    //    if (!active) { return; }
    //    LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_RGB);
    //    setSolidColor(color);
    //    LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);
    //}

    //public void setMouseColorFlashing(int color, int dutyCycle)
    //{
    //    if (!active) { return; }
    //    LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);
    //    setFlashingColor(color, dutyCycle);
    //    LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);
    //}

    //public void setMouseColorPulsing(int color, int dutyCycle)
    //{
    //    if (!active) { return; }
    //    LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);
    //    setPulsingColor(color, dutyCycle);
    //    LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);
    //}

    //Init and shutdown methods
    public static LightHandler init()
    {
        LogHelper.info("LogitechGLights starting...");
        LightHandler handler = new LightHandler();
        LogiLED.LogiLedInit();
        LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);
        handler.setSolidColor(0x000000);
        LogHelper.info("LogitechGLights started");
        return handler;
    }

    public void initBaseLighting()
    {
        for (KeyBinding binding : Minecraft.getMinecraft().gameSettings.keyBindings)
        {
            setSolidColorOnKey(binding.getKeyCode(), ConfigHandler.getColorForKeyCategory(binding.getKeyCategory()));
        }
        EventHandler.INSTANCE.reinit();
    }

    public void shutdown(boolean silent)
    {
        if (!active) { return; }

        if (!silent) { LogHelper.info("LogitechGLights shutting down..."); }
        active = false;
        EventHandler.INSTANCE.reinit();
        LogiLED.LogiLedShutdown();
    }

    public void restart(boolean silent)
    {
        if (active) { return; }

        if (!silent) { LogHelper.info("LogitechGLights starting..."); }
        LogiLED.LogiLedInit();
        LogiLED.LogiLedSetTargetDevice(LogiLED.LOGI_DEVICETYPE_PERKEY_RGB);
        active = true;
        setSolidColor(0x000000);
        initBaseLighting();
        if (!silent) { LogHelper.info("LogitechGLights started"); }

        for (Method m : restartCallbacks)
        {
            try { m.invoke(null); }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                LogHelper.error("An error occured while calling a restartCallback method!");
                e.printStackTrace();
            }
        }
    }

    //Helper methods
    private static int[] getColorsFromHex(int color)
    {
        int[] colors = new int[3];
        colors[0] = (int) (((color >> 16 & 255) / 255.0F) * 100F);
        colors[1] = (int) (((color >> 8 & 255) / 255.0F) * 100F);
        colors[2] = (int) (((color & 255) / 255.0F) * 100F);
        return colors;
    }

    public void addRestartCallback(Method callback)
    {
        if (callback.getParameterCount() != 0) { throw new IllegalArgumentException("RestartCallback methods can't have parameters!"); }

        restartCallbacks.add(callback);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { return; }

        shutdown(false);
        restart(false);
    }
}