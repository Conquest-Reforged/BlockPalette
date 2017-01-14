package me.dags.blockpalette.gui;

import me.dags.blockpalette.color.ColorMode;
import me.dags.blockpalette.palette.PaletteMain;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;

import java.util.*;

/**
 * @author dags <dags@dags.me>
 */
public class UIConfig {

    private static final int ROW_HEIGHT = 22;

    private final Map<String, List<GuiButton>> left = new LinkedHashMap<>();
    private final Map<String, List<GuiButton>> right = new LinkedHashMap<>();

    private final PaletteMain main;
    private final int button_width = 120;
    private int left_margin;
    private int right_margin;
    private int left_top = 10;
    private int right_top = 10;

    public UIConfig(final PaletteMain main) {
        this.main = main;

        Gui.ActionButton modeButton = new Gui.ActionButton(Config.match_textures ? "Mode: Match Texture" : "Mode: Match Color", mode);
        modeButton.setWidth(button_width);

        Gui.ActionButton filterButton = new Gui.ActionButton(Config.filter_variants ? "Inventory Filter: On" : "Inventory Filter: Off", filter);
        filterButton.setWidth(button_width);

        Gui.ActionButton colorModeButton = new Gui.ActionButton("Mode: " + Config.color_mode.display, color_mode);
        colorModeButton.setWidth(button_width);

        Gui.ActionButton hueButton = new Gui.ActionButton("Show Hue: " + Config.show_hue, hue);
        hueButton.setWidth(button_width);

        GuiSlider animationSlider = new Gui.ActionSlider(1F, 10F, Config.animation_speed, animation, none);
        animationSlider.setWidth(button_width);

        GuiSlider groupSlider = new Gui.ActionSlider(0F, 5F, Config.group_size, group, refresh);
        groupSlider.setWidth(button_width);

        GuiSlider angleSlider = new Gui.ActionSlider(0F, 120F, Config.angle, angle, refresh);
        angleSlider.setWidth(button_width);

        GuiSlider leniencySlider = new Gui.ActionSlider(0F, 1F, Config.leniency, leniency, refresh);
        leniencySlider.setWidth(button_width);

        Gui.ActionButton refreshPalette = new Gui.ActionButton("Refresh", refresh);
        refreshPalette.setWidth(button_width);


        addToSection(left, "general", modeButton);
        addToSection(left, "general", filterButton);
        addToSection(left, "general", animationSlider);

        addToSection(right, "color", colorModeButton);
        addToSection(right, "color", hueButton);
        addToSection(right, "color", angleSlider);
        addToSection(right, "color", groupSlider);
        addToSection(right, "color", leniencySlider);
        addToSection(right, "color", refreshPalette);

        setSectionVisibility(right, "color", !Config.match_textures);

        resize();
    }

    private void resize() {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        left_margin = 2;
        right_margin = resolution.getScaledWidth() - 2 - button_width;
        int top = Math.min(calcTop(left, resolution), calcTop(right, resolution));
        left_top = top;
        right_top = top;
    }

    private int calcTop(Map<String, List<GuiButton>> side, ScaledResolution resolution) {
        int rows = 0;
        for (Map.Entry<String, List<GuiButton>> entry : side.entrySet()) {
            rows += entry.getValue().size();
        }
        int space = resolution.getScaledHeight() - (rows * ROW_HEIGHT);
        return space / 2;
    }

    private List<GuiButton> getSection(Map<String, List<GuiButton>> side, String section) {
        List<GuiButton> buttons = side.get(section);
        return buttons == null ? Collections.<GuiButton>emptyList() : buttons;
    }

    private void addToSection(Map<String, List<GuiButton>> side, String section, GuiButton button) {
        List<GuiButton> buttons = side.get(section);
        if (buttons == null) {
            side.put(section, buttons = new ArrayList<>());
        }
        buttons.add(button);
    }

    private void setSectionVisibility(Map<String, List<GuiButton>> side, String section, boolean value) {
        for (GuiButton button : getSection(side, section)) {
            button.visible = value;
        }
    }

    private void draw(Map<String, List<GuiButton>> side, int mouseX, int mouseY, int left, int top) {
        for (Map.Entry<String, List<GuiButton>> entry : side.entrySet()) {
            for (GuiButton button : entry.getValue()) {
                if (button.visible) {
                    button.yPosition = top;
                    button.xPosition = left;
                    button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
                    top += ROW_HEIGHT;
                }
            }
        }
    }

    private void mouseClicked(Map<String, List<GuiButton>> side, int mouseX, int mouseY, int btn) {
        for (Map.Entry<String, List<GuiButton>> entry : side.entrySet()) {
            for (GuiButton button : entry.getValue()) {
                button.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
            }
        }
    }

    private void mouseReleased(Map<String, List<GuiButton>> side, int mouseX, int mouseY, int btn) {
        for (Map.Entry<String, List<GuiButton>> entry : side.entrySet()) {
            for (GuiButton button : entry.getValue()) {
                button.mouseReleased(mouseX, mouseY);
            }
        }
    }

    public void draw(int mouseX, int mouseY) {
        draw(left, mouseX, mouseY, left_margin, left_top);
        draw(right, mouseX, mouseY, right_margin, right_top);
    }

    public void mouseClick(int mouseX, int mouseY, int btn) {
        mouseClicked(left, mouseX, mouseY, btn);
        mouseClicked(right, mouseX, mouseY, btn);
    }

    public void mouseRelease(int mouseX, int mouseY, int btn) {
        mouseReleased(left, mouseX, mouseY, btn);
        mouseReleased(right, mouseX, mouseY, btn);
    }

    public void onResize(Minecraft mc, int w, int h) {
        resize();
    }

    public void onClose() {
        Config.save();
    }

    private static final Gui.Action none = new Gui.Action() {
        public void onAction(GuiButton button) {}
    };

    private final Gui.Action refresh = new Gui.Action() {
        public void onAction(GuiButton button) {
            main.newPalette();
        }
    };

    private final Gui.Action mode = new Gui.Action() {
        public void onAction(GuiButton button) {
            Config.match_textures = !Config.match_textures;
            button.displayString = Config.match_textures ? "Mode: Match Texture" : "Mode: Match Color";
            setSectionVisibility(right, "color", !Config.match_textures);
            main.newPalette();
        }
    };

    private final Gui.Action filter = new Gui.Action() {
        public void onAction(GuiButton button) {
            Config.filter_variants = !Config.filter_variants;
            button.displayString = Config.filter_variants ? "Inventory Filter: On" : "Inventory Filter: Off";

            // Update CreativeUI if we are overlaying it with the palette
            if (main.getCurrentPalette().isOverlay()) {
                GuiContainerCreative creative = new GuiContainerCreative(Minecraft.getMinecraft().thePlayer);
                Minecraft.getMinecraft().displayGuiScreen(creative);
            }
        }
    };

    private final Gui.Action color_mode = new Gui.Action() {
        public void onAction(GuiButton button) {
            Config.color_mode = ColorMode.next(Config.color_mode);
            button.displayString = "Mode: " + Config.color_mode.display;
            main.newPalette();
        }
    };

    private final Gui.Action hue = new Gui.Action() {
        public void onAction(GuiButton button) {
            Config.show_hue = !Config.show_hue;
            button.displayString = "Show Hue: " + Config.show_hue;
        }
    };

    private static final GuiSlider.FormatHelper animation = new GuiSlider.FormatHelper() {
        @Override
        public String getText(int id, String name, float value) {
            if ((int) value != Config.animation_speed) {
                Config.animation_speed = (int) value;
            }
            return String.format("Animation Speed: %s", Config.animation_speed);
        }
    };

    private static final GuiSlider.FormatHelper angle = new GuiSlider.FormatHelper() {
        @Override
        public String getText(int id, String name, float value) {
            if ((int) value != Config.angle) {
                Config.angle = (int) value;
            }
            return String.format("Angle: %s", Config.angle);
        }
    };

    private static final GuiSlider.FormatHelper group = new GuiSlider.FormatHelper() {
        @Override
        public String getText(int id, String name, float value) {
            if ((int) value != Config.group_size) {
                Config.group_size = (int) value;
            }
            return String.format("Group Size: %s", Config.group_size);
        }
    };

    private static final GuiSlider.FormatHelper leniency = new GuiSlider.FormatHelper() {
        @Override
        public String getText(int id, String name, float value) {
            if (value != Config.leniency) {
                Config.leniency = value;
            }
            return String.format("Leniency: %.2f", Config.leniency);
        }
    };
}
