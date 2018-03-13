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

package XFactHD.lgl.api;

import XFactHD.lgl.LogitechGLights;
import XFactHD.lgl.client.gui.GuiColorConfig;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class LogitechGLightsAPI
{
    /**
     * Add a description for a key category in the color config gui
     * @param keyCategory The key category the description belongs to
     * @param description The description for the key category
     * */
    public static void addKeyCategoryDescription(String keyCategory, String description)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        GuiColorConfig.addKeyCategoryDescription(keyCategory, description);
    }

    /**
     * Set all keys to a solid color
     * @param color The color in the format 0xRRGGBB
     * */
    public static void setSolidColor(int color)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.setSolidColor(color);
    }

    /**
     * Set all keys to flash in one color
     * @param color The color in the format 0xRRGGBB
     * @param dutyCycle The time the lights will be on and off in milliseconds
     * */
    public static void setFlashingColor(int color, int dutyCycle)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.setFlashingColor(color, dutyCycle);
    }

    /**
     * Set all keys to pulse in one color
     * @param color The color in the format 0xRRGGBB
     * @param dutyCycle The time it takes the lights to fade in and out in milliseconds
     * */
    public static void setPulsingColor(int color, int dutyCycle)
    {

        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }
        LogitechGLights.handler.setPulsingColor(color, dutyCycle);
    }

    /**
     * Set one key to a solid color
     * @param key The key code of the key
     * @param color The color in the format 0xRRGGBB
     * */
    public static void setSolidColorOnKey(int key, int color)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.setSolidColorOnKey(key, color);
    }

    /**
     * Set one key to flash in one color
     * @param key The key code of the key
     * @param color The color in the format 0xRRGGBB
     * @param dutyCycle The time the light will be on and off in milliseconds
     * */
    public static void setFlashingColorOnKey(int key, int color, int dutyCycle)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.setFlashingColorOnKey(key, color, dutyCycle);
    }

    /**
     * Set one key to flash in one color
     * @param key The key code of the key
     * @param color The color in the format 0xRRGGBB
     * @param dutyCycle The time it takes the lights to fade in and out in milliseconds
     * */
    public static void setPulsingColorOnKey(int key, int color, int dutyCycle)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.setPulsingColorOnKey(key, color, dutyCycle);
    }

    /**
     * Stop all running effects like flashing keys
     * */
    public static void stopEffects()
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.stopEffects();
    }

    /**
     * Save the current lighting settings to be restored late with restoreLastLighting()
     * Should be called before activating temporary effects
     * @implNote Does currently not save the lighting correctly
     * */
    public static void saveCurrentLighting()
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.saveCurrentLighting();
    }

    /**
     * Restore the lighting settings previously save with saveCurrentLighting()
     * Should be called after temporary effects to restore the old settings
     * @implNote Currently uses a stupid workaround to be atleast somewhat useful because saveCurrentLighting() doesn't work
     * */
    public static void restoreLastLighting()
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.restoreLastLighting();
    }

    /**
     * Shut down and disable the LED SDK and return to the base profile defined in the Logitech Gaming Software
     * @param silent Wether the shutdown should happen silently or put its state into the log
     * @implNote Should only be used if really necessary!
     * */
    public static void shutdown(boolean silent)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.shutdown(silent);
    }

    /**
     * Restart the LED SDK after a call to shutdown()
     * @param silent Wether the restart should happen silently or put its state into the log
     * @implNote Should only be used if really necessary!
     * */
    public static void restart(boolean silent)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.restart(silent);
    }

    /**
     * Add a callback that gets called after restart() completes
     * Useful to reenable temporary effets that where destroyed by a shutdown and restart caused by the ResourceManager reloading or the window losing and regaining focus
     * */
    public static void addRestartCallback(Method callback)
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.addRestartCallback(callback);
    }

    public static void initBaseLighting()
    {
        if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) { throw new IllegalStateException("This method can't be called until PostInit is over!"); }

        LogitechGLights.handler.initBaseLighting();
    }
}