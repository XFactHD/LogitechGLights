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

package XFactHD.lgl.client.gui;

import XFactHD.lgl.LogitechGLights;
import XFactHD.lgl.client.utils.ConfigHandler;
import XFactHD.lgl.client.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class GuiColorConfig extends GuiScreen implements GuiPageButtonList.GuiResponder
{
    private static final HashMap<String, String> CAT_DESC_MAP = new HashMap<>();
    private static final Map<String, Integer> CATEGORY_ORDER = new HashMap<>();
    private static final Comparator<String> CATEGORY_SORTER = (s1, s2) -> {
        if (!CATEGORY_ORDER.containsKey(s1) && !CATEGORY_ORDER.containsKey(s2))
        {
            return s1.compareTo(s2);
        }
        else if (!CATEGORY_ORDER.containsKey(s1)) { return 1; }
        else if (!CATEGORY_ORDER.containsKey(s2)) { return -1; }
        else
        {
            return CATEGORY_ORDER.get(s1) - CATEGORY_ORDER.get(s2);
        }
    };
    private static final int guiWidth = 480;
    private static final int guiHeight = 240;
    private static final GuiSlider.FormatHelper formatter = (id, name, value) -> I18n.format(name) + ": " + (int)value;

    private int guiLeft;
    private int guiTop;

    private GuiScrollingListString list;
    private GuiSliderColorGradient sliderRed;
    private GuiSliderColorGradient sliderGreen;
    private GuiSliderColorGradient sliderBlue;
    private GuiSliderColorGradient sliderBrighten;
    private GuiSliderColorGradient sliderDarken;

    private boolean showingDemo = false;
    private String currentCategory = "key.categories.unknown";
    private String currentDescription = "";
    private int currentColor = 0xFFFFFFFF;

    public static void init()
    {
        CAT_DESC_MAP.put("key.categories.unknown", "desc.keycat.unknown.name");
        CAT_DESC_MAP.put("key.categories.dead", "desc.keycat.dead.name");
        CAT_DESC_MAP.put("key.categories.inventory.selected", "desc.keycat.selected.name");

        CATEGORY_ORDER.put("key.categories.movement", 1);
        CATEGORY_ORDER.put("key.categories.gameplay", 2);
        CATEGORY_ORDER.put("key.categories.inventory", 3);
        CATEGORY_ORDER.put("key.categories.creative", 4);
        CATEGORY_ORDER.put("key.categories.multiplayer", 5);
        CATEGORY_ORDER.put("key.categories.ui", 6);
        CATEGORY_ORDER.put("key.categories.misc", 7);
    }

    public static void addKeyCategoryDescription(String keyCategory, String description)
    {
        CAT_DESC_MAP.put(keyCategory, description);
    }

    @Override
    public void initGui()
    {
        ScaledResolution res = new ScaledResolution(mc);
        guiLeft = (res.getScaledWidth() / 2) - (guiWidth / 2);
        guiTop = (res.getScaledHeight() / 2) - (guiHeight / 2);

        GuiLabel label = new GuiLabel(mc.fontRenderer, 0, guiLeft + 10, guiTop + 10, 110, 8, 0xFFFFFF);
        label.setCentered();
        label.addLine("desc.categories.name");

        ArrayList<String> values = new ArrayList<>();
        String[] categories = ConfigHandler.INSTANCE.getCategoryMap().keySet().toArray(new String[ConfigHandler.INSTANCE.getCategoryMap().size()]);
        Arrays.sort(categories, CATEGORY_SORTER);
        for (String s : categories)
        {
            if (!values.contains(s))
            {
                values.add(s);
                if (s.equals("key.categories.inventory"))
                {
                    if (values.contains("key.categories.inventory.selected")) { values.remove("key.categories.inventory.selected"); }
                    values.add("key.categories.inventory.selected");
                }
            }
        }
        list = new GuiScrollingListString(mc, 0, guiLeft + 10, guiTop + 30, 110, 150, values, this);

        if (!values.isEmpty()) { currentDescription = I18n.format(CAT_DESC_MAP.getOrDefault(values.get(0), values.get(0))); }

        sliderRed =      new GuiSliderColorGradient(this, 1, guiLeft + 140, guiTop +  30, "desc.lgl:red.name",      0, 255, 255, formatter);
        sliderGreen =    new GuiSliderColorGradient(this, 2, guiLeft + 140, guiTop +  60, "desc.lgl:green.name",    0, 255, 255, formatter);
        sliderBlue =     new GuiSliderColorGradient(this, 3, guiLeft + 140, guiTop +  90, "desc.lgl:blue.name",     0, 255, 255, formatter);
        sliderBrighten = new GuiSliderColorGradient(this, 4, guiLeft + 140, guiTop + 120, "desc.lgl:brighten.name", 0, 255, 255, formatter);
        sliderDarken =   new GuiSliderColorGradient(this, 5, guiLeft + 140, guiTop + 150, "desc.lgl:darken.name",   0, 255,   0, formatter);

        sliderRed.setColorEnd(0xFFFF0000);
        sliderGreen.setColorEnd(0xFF00FF00);
        sliderBlue.setColorEnd(0xFF0000FF);
        sliderBrighten.setColorStart(0xFFFFFFFF);
        sliderDarken.setColorStart(0xFFFFFFFF);
        sliderDarken.setColorEnd(0xFF000000);

        GuiButton close = new GuiButton(6, guiLeft + guiWidth - 110, guiTop + 210, 100, 20, "Close");
        GuiButton load = new GuiButton(7, guiLeft + 395, guiTop + 30, 60, 20, "Load");
        GuiButton demo = new GuiButton(8, guiLeft + 395, guiTop + 58, 60, 20, "Show");
        GuiButton save = new GuiButton(9, guiLeft + 395, guiTop + 86, 60, 20, "Save");

        labelList.add(label);
        buttonList.add(sliderRed);
        buttonList.add(sliderGreen);
        buttonList.add(sliderBlue);
        buttonList.add(sliderBrighten);
        buttonList.add(sliderDarken);
        buttonList.add(close);
        buttonList.add(load);
        buttonList.add(demo);
        buttonList.add(save);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        mc.renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/gui/gui_color_config.png"));
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 240, guiHeight);
        drawTexturedModalRect(guiLeft + 240, guiTop, 16, 0, 240, guiHeight);
        list.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawRect(guiLeft + 10, guiTop + 190, guiLeft + 120, guiTop + 232, 0xFF808080);
        drawRect(guiLeft + 11, guiTop + 191, guiLeft + 119, guiTop + 231, 0xC0101010);

        int index = 0;
        for (String s : currentDescription.split("\\|"))
        {
            fontRenderer.drawString(fontRenderer.trimStringToWidth(s, 106), guiLeft + 12, guiTop + 192 + (10 * index), 0xFFFFFF);
            index++;
        }

        mc.renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/gui/gui_elements.png"));
        drawTexturedModalRect(guiLeft + 320, guiTop + 30, 0, 0, 62, 62);
        drawRect(guiLeft + 321, guiTop + 31, guiLeft + 381, guiTop +  91, currentColor);
        drawRect(guiLeft + 321, guiTop + 93, guiLeft + 381, guiTop + 106, 0xFF808080);
        drawRect(guiLeft + 322, guiTop + 94, guiLeft + 380, guiTop + 105, 0xC0101010);
        fontRenderer.drawString("0x" + Integer.toHexString(currentColor).toUpperCase(Locale.ENGLISH).substring(2), guiLeft + 328, guiTop + 96, 0xFFFFFF);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        float red = sliderRed.getSliderValue();
        float green = sliderGreen.getSliderValue();
        float blue = sliderBlue.getSliderValue();

        float brighten = sliderBrighten.getSliderValue();
        float darken = sliderDarken.getSliderValue();

        currentColor = getHexColorFromRGBInts((int)red, (int)green, (int)blue);
        sliderBrighten.setColorEnd(currentColor);
        sliderDarken.setColorStart(currentColor);

        if (red == 0) { red = 255F - brighten; }
        else if (red != 255) { red += (255F - red) * (1F - (brighten / 255F)); }
        if (green == 0) { green = 255F - brighten; }
        else if (green != 255) { green += (255F - green) * (1F - (brighten / 255F)); }
        if (blue == 0) { blue = 255F - brighten; }
        else if (blue != 255) { blue += (255F - blue) * (1F - (brighten / 255F)); }

        if (red > 0) { red *= 1F - (darken / 255F); }
        if (green > 0) { green *= 1F - (darken / 255F); }
        if (blue > 0) { blue *= 1F - (darken / 255F); }

        red = Math.min(Math.max(0F, red), 255F);
        green = Math.min(Math.max(0F, green), 255F);
        blue = Math.min(Math.max(0F, blue), 255F);

        currentColor = getHexColorFromRGBInts((int)red, (int)green, (int)blue);

        //sliderRed.setColorBar(  getHexColorFromRGBInts((int)red,          0,         0));
        //sliderGreen.setColorBar(getHexColorFromRGBInts(       0, (int)green,         0));
        //sliderBlue.setColorBar( getHexColorFromRGBInts(       0,          0, (int)blue));
        //sliderBrighten.setColorBar(currentColor);
        //sliderDarken.setColorBar(currentColor);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
            case 6:
            {
                if (showingDemo)
                {
                    LogitechGLights.handler.setSolidColor(0x000000);
                    LogitechGLights.handler.initBaseLighting();
                }
                mc.player.closeScreen();
                break;
            }
            case 7:
            {
                currentColor = ConfigHandler.getColorForKeyCategory(currentCategory);

                int red = currentColor >> 16 & 255;
                int green = currentColor >> 8 & 255;
                int blue = currentColor & 255;

                sliderRed.setSliderValue(red, true);
                sliderGreen.setSliderValue(green, true);
                sliderBlue.setSliderValue(blue, true);
                sliderBrighten.setSliderValue(255, true);
                sliderDarken.setSliderValue(0, true);
                break;
            }
            case 8:
            {
                if (showingDemo)
                {
                    showingDemo = false;
                    button.packedFGColour = 0xFFFFFFFF;
                    LogitechGLights.handler.setSolidColor(0x000000);
                    LogitechGLights.handler.initBaseLighting();
                }
                else
                {
                    showingDemo = true;

                    int color = currentColor;
                    int red = color >> 16 & 255;
                    int green = color >> 8 & 255;
                    int blue = color & 255;
                    color = getHexColorFromRGBInts(red, green, blue);

                    switch (currentCategory)
                    {
                        case "key.categories.dead": LogitechGLights.handler.setSolidColor(color); break;
                        case "key.categories.unknown": LogitechGLights.handler.setSolidColor(color); break; //FIXME: not the best solution to be honest
                        case "key.categories.inventory.selected":
                        {
                            LogitechGLights.handler.setSolidColor(0x000000);
                            for (KeyBinding binding : mc.gameSettings.keyBindsHotbar)
                            {
                                LogitechGLights.handler.setSolidColorOnKey(binding.getKeyCode(), color);
                            }
                            break;
                        }
                        default:
                        {
                            LogitechGLights.handler.setSolidColor(0x000000);
                            for (KeyBinding binding : mc.gameSettings.keyBindings)
                            {
                                if (binding.getKeyCategory().equals(currentCategory))
                                {
                                    LogitechGLights.handler.setSolidColorOnKey(binding.getKeyCode(), color);
                                }
                            }
                            break;
                        }
                    }
                    button.packedFGColour = 0xFF00FF00;
                }
                break;
            }
            case 9:
            {
                int color = currentColor;
                int red = color >> 16 & 255;
                int green = color >> 8 & 255;
                int blue = color & 255;
                color = getHexColorFromRGBInts(red, green, blue);
                ConfigHandler.setColorForKeyCategory(currentCategory, color);
                if (!currentCategory.equals("key.categories.dead") && !currentCategory.equals("key.categories.unknown"))
                {
                    LogitechGLights.handler.setSolidColor(0x000000);
                    LogitechGLights.handler.initBaseLighting();
                }
                break;
            }
        }
    }

    @Override
    public void setEntryValue(int id, boolean value) {}

    @Override
    public void setEntryValue(int id, float value)
    {
        if (id >= 1 && id <= 5 && showingDemo)
        {
            int color = currentColor;
            int red = color >> 16 & 255;
            int green = color >> 8 & 255;
            int blue = color & 255;
            color = getHexColorFromRGBInts(red, green, blue);

            switch (currentCategory)
            {
                case "key.categories.dead": LogitechGLights.handler.setSolidColor(color); break;
                case "key.categories.unknown": LogitechGLights.handler.setSolidColor(color); break; //FIXME: not the best solution to be honest
                case "key.categories.inventory.selected":
                {
                    LogitechGLights.handler.setSolidColor(0x000000);
                    for (KeyBinding binding : mc.gameSettings.keyBindsHotbar)
                    {
                        LogitechGLights.handler.setSolidColorOnKey(binding.getKeyCode(), color);
                    }
                    break;
                }
                default:
                {
                    LogitechGLights.handler.setSolidColor(0x000000);
                    for (KeyBinding binding : mc.gameSettings.keyBindings)
                    {
                        if (binding.getKeyCategory().equals(currentCategory))
                        {
                            LogitechGLights.handler.setSolidColorOnKey(binding.getKeyCode(), color);
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void setEntryValue(int id, String value)
    {
        if (id == list.id)
        {
            currentDescription = I18n.format(CAT_DESC_MAP.getOrDefault(value, value));
            currentCategory = value;
        }
    }

    @SuppressWarnings("NumericOverflow")
    private static int getHexColorFromRGBInts(int red, int green, int blue)
    {
        return (255 << 24) | (red << 16) | (green << 8) | (blue);
    }

    private static class GuiScrollingListString extends GuiScrollingList
    {
        private int id;
        private FontRenderer font;
        private ArrayList<String> values;
        private GuiPageButtonList.GuiResponder responder;

        public GuiScrollingListString(Minecraft mc, int id, int x, int y, int width, int height, ArrayList<String> values, GuiPageButtonList.GuiResponder responder)
        {
            super(mc, width, height, y, y + height, x, 12, mc.displayWidth, mc.displayHeight);
            this.id = id;
            this.font = mc.fontRenderer;
            this.values = values;
            this.responder = responder;
            if (!values.isEmpty()) { selectedIndex = 0; }
        }

        @Override
        protected int getSize()
        {
            return values.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick)
        {
            if (!doubleClick)
            {
                responder.setEntryValue(id, values.get(index));
            }
        }

        @Override
        protected boolean isSelected(int index)
        {
            return index == selectedIndex;
        }

        @Override
        protected void drawBackground()
        {

        }

        @Override
        protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
        {
            font.drawString(font.trimStringToWidth(I18n.format(values.get(slotIdx)), listWidth - 10), this.left + 3 , slotTop, 0xFFFFFF);
        }
    }

    private static class GuiSliderColorGradient extends GuiSlider
    {
        private int colorBar = 0xFFFFFFFF;
        private int colorStart = 0xFF000000;
        private int colorEnd = 0xFFFFFFFF;

        public GuiSliderColorGradient(GuiPageButtonList.GuiResponder guiResponder, int idIn, int x, int y, String nameIn, float minIn, float maxIn, float defaultValue, GuiSlider.FormatHelper formatter)
        {
            super(guiResponder, idIn, x, y, nameIn, minIn, maxIn, defaultValue, formatter);
        }

        @Override
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
        {
            super.mouseDragged(mc, mouseX, mouseY);
            if (this.visible)
            {
                drawHorizontalGradientRect(x + 1, y + 1, x + width - 1, y + height - 1, colorStart, colorEnd);

                float alpha = (float)(colorBar >> 24 & 255) / 255.0F;
                float red = (float)(colorBar >> 16 & 255) / 255.0F;
                float green = (float)(colorBar >> 8 & 255) / 255.0F;
                float blue = (float)(colorBar & 255) / 255.0F;

                GlStateManager.color(red, green, blue, alpha);
                this.drawTexturedModalRect(this.x + (int)(this.getSliderPosition() * (float)(this.width - 8)),     this.y,   0, 66, 4, 20);
                this.drawTexturedModalRect(this.x + (int)(this.getSliderPosition() * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
            }
        }

        //TODO: find a better solution for this as the bar is almost invisible when the color is black
        //public void setColorBar(int colorBar) { this.colorBar = colorBar; }

        public void setColorStart(int colorStart)
        {
            this.colorStart = colorStart;
        }

        public void setColorEnd(int colorEnd)
        {
            this.colorEnd = colorEnd;
        }

        /**
         * Draws a rectangle with a horizontal gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
         * leftColor, rightColor
         */
        private void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
        {
            float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
            float startRed = (float)(startColor >> 16 & 255) / 255.0F;
            float startGreen = (float)(startColor >> 8 & 255) / 255.0F;
            float startBlue = (float)(startColor & 255) / 255.0F;
            float endAlpha = (float)(endColor >> 24 & 255) / 255.0F;
            float endRed = (float)(endColor >> 16 & 255) / 255.0F;
            float endGreen = (float)(endColor >> 8 & 255) / 255.0F;
            float endBlue = (float)(endColor & 255) / 255.0F;
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.shadeModel(7425);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos((double)right, (double)top,    (double)this.zLevel).color(endRed,   endGreen,   endBlue,   endAlpha).endVertex();
            bufferbuilder.pos((double)left,  (double)top,    (double)this.zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
            bufferbuilder.pos((double)left,  (double)bottom, (double)this.zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
            bufferbuilder.pos((double)right, (double)bottom, (double)this.zLevel).color(endRed,   endGreen,   endBlue,   endAlpha).endVertex();
            tessellator.draw();
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
        }
    }
}