package me.dags.blockpalette.palette;

import me.dags.blockpalette.color.ColorF;
import me.dags.blockpalette.color.ColorMode;
import me.dags.blockpalette.creative.PickMode;
import me.dags.blockpalette.gui.Animation;
import me.dags.blockpalette.gui.Hotbar;
import me.dags.blockpalette.gui.Settings;
import me.dags.blockpalette.gui.UI;
import me.dags.blockpalette.util.Config;
import me.dags.blockpalette.util.Render;
import me.dags.blockpalette.util.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class PaletteScreen extends GuiScreen {

    private static final ResourceLocation BUTTON = new ResourceLocation("blockpalette", "textures/gui/button_mask.png");

    private final Settings paletteSettings = new Settings();
    private final Settings colorSettings = new Settings();
    private final Value<Boolean> showSettings = Value.of(Config.show_settings);
    private final Value<Boolean> matchMode = Value.of(Config.match_textures);
    private final Value<PickMode> pickMode = Value.of(Config.pick_mode);
    private final Value<Boolean> holdKey = Value.of(Config.hold_key);
    private final Value<Boolean> tooltips = Value.of(Config.show_tooltips);
    private final Value<Integer> highlightRed = Value.instant(Config.highlight_red);
    private final Value<Integer> highlightGreen = Value.instant(Config.highlight_green);
    private final Value<Integer> highlightBlue = Value.instant(Config.highlight_blue);
    private final Value<Float> highlightScale = Value.instant(Config.highlight_scale);

    private final Value<ColorMode> colorMode = Value.of(Config.color_mode);
    private final Value<Float> colorOpacity = Value.instant(Config.color_opacity);
    private final Value<Integer> colorAngle = Value.of(Config.angle);
    private final Value<Integer> colorGroupSize = Value.of(Config.group_size);
    private final Value<Float> colorLeniency = Value.of(Config.leniency);
    private final Value<Float> grayPoint = Value.of(Config.gray_point);
    private final Value<Float> alphaPoint = Value.of(Config.alpha_point);

    private final UI.AreaCycler<Boolean> toggleMode = new UI.AreaCycler<>(matchMode, new Boolean[]{true, false}, BUTTON);
    private final UI.AreaCycler<Boolean> toggleSettings = new UI.AreaCycler<>(showSettings, new Boolean[]{true, false}, BUTTON);
    private final List<GuiButton> buttons = new ArrayList<>();

    private final Hotbar hotbar;

    private final Runnable refresh = () -> {
        PaletteScreen.this.main.newPaletteCreative(PaletteScreen.this.main.getPalette().getCenter());
        refreshScreen();
    };

    private final PaletteMain main;
    private final Minecraft minecraft;
    private int width = 0;
    private int height = 0;
    private boolean isCreativeOverlay = false;

    public PaletteScreen(PaletteMain main) {
        this.main = main;
        hotbar = new Hotbar(main.getPalette().getStackUnderMouse(), main.getPalette().getSelectedStack());

        minecraft = Minecraft.getMinecraft();

        paletteSettings.add(new UI.Cycler<>(pickMode, PickMode.values(), "palette.control.pickmode"), pickMode);
        paletteSettings.add(new UI.Cycler<>(holdKey, new Boolean[]{true, false}, "palette.control.hold"), "palette.tooltip.hold");
        paletteSettings.add(new UI.Cycler<>(tooltips, new Boolean[]{true, false}, "palette.control.tooltips"), "palette.tooltip.tooltips");
        paletteSettings.add(new UI.Label("palette.control.highlight.color.label", ColorF.rgb(255, 255, 255)));
        paletteSettings.add(new UI.IntSlider("palette.control.highlight.red", 0, 255, highlightRed),"palette.tooltip.highlight.color");
        paletteSettings.add(new UI.IntSlider("palette.control.highlight.green", 0, 255, highlightGreen), "palette.tooltip.highlight.color");
        paletteSettings.add(new UI.IntSlider("palette.control.highlight.blue", 0, 255, highlightBlue), "palette.tooltip.highlight.color");
        paletteSettings.add(new UI.Label("palette.control.highlight.scale.label", ColorF.rgb(255, 255, 255)));
        paletteSettings.add(new UI.FloatSlider("palette.control.highlight.scale", 1F, 1.5F, highlightScale), "palette.tooltip.highlight.scale");

        colorSettings.add(new UI.Label("palette.control.mode.label", 0xFFFFFF));
        colorSettings.add(new UI.Cycler<>(colorMode, ColorMode.values(), "%s"));
        colorSettings.add(new UI.FloatSlider("palette.control.opacity",0F, 1F, colorOpacity));

        colorSettings.add(new UI.Label("palette.control.settings.label", 0xFFFFFF));
        colorSettings.add(new UI.IntSlider("palette.control.angle", 0, 120, colorAngle), "palette.tooltip.angle");
        colorSettings.add(new UI.IntSlider("palette.control.groups", 1, 5, colorGroupSize), "palette.tooltip.groups");
        colorSettings.add(new UI.FloatSlider("palette.control.leniency", 0F, 1F, colorLeniency), "palette.tooltip.leniency");
        colorSettings.add(new UI.FloatSlider("palette.control.gray", 0F, 1F, grayPoint), "palette.tooltip.gray");
        colorSettings.add(new UI.FloatSlider("palette.control.alpha", 0F, 1F, alphaPoint), "palette.tooltip.alpha");
        colorSettings.add(new UI.Label("", 0xFFFFFF));
        colorSettings.add(new UI.Button("palette.control.refresh", refresh));

        buttons.add(toggleMode);
        buttons.add(toggleSettings);
    }

    @Override
    public void initGui() {
        resize();
        init();
        KeyBinding.setKeyBindState(main.show.getKeyCode(), true);
    }

    @Override
    public void onResize(Minecraft minecraft, int w, int h) {
        super.onResize(minecraft, w, h);
        resize();
        setupAnimations();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!listeningToKeyRelease() && !isCreativeOverlay && isMouseBind(mouseButton)) {
            minecraft.setIngameFocus();
            return;
        }

        paletteSettings.mousePressed(minecraft, mouseX, mouseY);
        colorSettings.mousePressed(minecraft, mouseX, mouseY);

        for (GuiButton button : buttons) {
            button.mousePressed(minecraft, mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);

        if (isMouseBind(mouseButton)) {
            if (listeningToKeyRelease()) {
                KeyBinding.setKeyBindState(main.show.getKeyCode(), false);
                return;
            }
        }

        if (mouseButton == 0 && Keyboard.isKeyDown(PaletteMain.switchKeyID) && main.getPalette().getStackUnderMouse().isPresent()) {
            main.newPaletteCreative(main.getPalette().getStackUnderMouse().get());
            refresh.run();
            return;
        }

        if (hotbar.mouseRelease(mouseX, mouseY)) {
            return;
        }

        main.getPalette().mouseRelease(mouseX, mouseY, mouseButton);

        paletteSettings.mouseReleased(mouseX, mouseY);
        colorSettings.mouseReleased(mouseX, mouseY);
        main.getPalette().setHighlightColor(highlightRed.get(), highlightGreen.get(), highlightBlue.get());
        main.getPalette().setHighlightRadius(highlightScale.get());

        for (GuiButton button : buttons) {
            button.mouseReleased(mouseX, mouseY);
        }
    }

    @Override
    public void keyTyped(char key, int code) throws IOException {
        super.keyTyped(key, code);
        hotbar.keyTyped(key, code);

        if (!isCreativeOverlay && code == main.show.getKeyCode()) {
            if (Config.pick_mode == PickMode.MOUSE || !Config.hold_key) {
                minecraft.setIngameFocus();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        hotbar.onClose();
        main.getPalette().close();
        main.newPaletteCreative(null);
        Config.save();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        KeyBinding.updateKeyBindState();

       // drawGradientRect(0, 0, this.width, this.height, 0x777777, -804253680);
       // Gui.drawRect(0, 0, width, height, 0x88000000);

        paletteSettings.draw(minecraft, mouseX, mouseY, partialTicks);
        colorSettings.draw(minecraft, mouseX, mouseY, partialTicks);

        hotbar.draw(mouseX, mouseY, partialTicks);
        main.getPalette().drawScreen(mouseX, mouseY, partialTicks);

        for (GuiButton button : buttons) {
            button.drawButton(minecraft, mouseX, mouseY, partialTicks);
        }

        if (Config.show_tooltips) {
            Render.cleanup();
            Render.beginTooltips();
            paletteSettings.drawTooltips(mouseX, mouseY, partialTicks);
            colorSettings.drawTooltips(mouseX, mouseY, partialTicks);
            Render.endTooltips();
        }

        if (listeningToKeyRelease() && keybindReleased()) {
            minecraft.setIngameFocus();
        }
    }

    private void addOption(GuiButton button) {
        paletteSettings.add(button);
    }

    private void addColor(GuiButton button) {
        colorSettings.add(button);
    }

    private boolean listeningToKeyRelease() {
        return !isCreativeOverlay && Config.hold_key;
    }

    private boolean keybindReleased() {
        return !main.show.isKeyDown();
    }

    private boolean isMouseBind(int code) {
        return code - 100 == main.show.getKeyCode();
    }

    public void setCreativeOverlay(boolean overlay) {
        this.isCreativeOverlay = overlay;
    }

    private void refreshScreen() {
        ScaledResolution resolution = new ScaledResolution(minecraft);
        main.getPalette().resize(resolution.getScaledWidth(), resolution.getScaledHeight());
        main.getPalette().setHighlightColor(highlightRed.get(), highlightGreen.get(), highlightBlue.get());

        int centerX = resolution.getScaledWidth() / 2;
        int settingsWidth = centerX - main.getPalette().getWidth() - 10;
        paletteSettings.setSize(settingsWidth, height);
        colorSettings.setSize(settingsWidth, height);

        if (Config.show_settings && !paletteSettings.onScreen(width, height)) {
            paletteSettings.open();
        }
        if (!Config.match_textures && !colorSettings.onScreen(width, height)) {
            colorSettings.open();
        }
    }

    private void resize() {
        ScaledResolution resolution = new ScaledResolution(minecraft);
        width = resolution.getScaledWidth();
        height = resolution.getScaledHeight();
        main.getPalette().resize(resolution.getScaledWidth(), resolution.getScaledHeight());
        hotbar.init(width, height);

        int centerX = resolution.getScaledWidth() / 2;
        int centerY = resolution.getScaledHeight() / 2;
        int settingsWidth = centerX - main.getPalette().getWidth() - 10;

        paletteSettings.setSize(settingsWidth, height);
        paletteSettings.setPosition(-paletteSettings.getWidth(), 0);

        colorSettings.setPosition(width, 0);
        colorSettings.setSize(settingsWidth, height);

        toggleSettings.x = centerX - 80 - 12;
        toggleSettings.y = centerY - 78 - 12;
        toggleSettings.width = 24;
        toggleSettings.height = 24;

        toggleMode.x = centerX - 30 - 12;
        toggleMode.y = centerY - 106 - 12;
        toggleMode.width = 24;
        toggleMode.height = 24;

        setupAnimations();
    }

    private void setupAnimations() {
        int time = 10;

        int paletteStartX = -paletteSettings.getWidth();
        int paletteEndX = 0;
        int paletteTop = 0;
        Animation paletteOpen = new Animation(paletteSettings, time, paletteStartX, paletteTop, paletteEndX, paletteTop);
        Animation paletteClose = new Animation(paletteSettings, time, paletteEndX, paletteTop, paletteStartX, paletteTop);
        paletteSettings.setAnimations(paletteOpen, paletteClose);

        int colorStartX = width;
        int colorEndX = width - colorSettings.getWidth();
        int colorTop = 0;
        Animation colorOpen = new Animation(colorSettings, time, colorStartX, colorTop, colorEndX, colorTop);
        Animation colorClose = new Animation(colorSettings, time, colorEndX, colorTop, colorStartX, colorTop);
        colorSettings.setAnimations(colorOpen, colorClose);
    }

    private void init() {
        main.getPalette().setHighlightColor(highlightRed.get(), highlightGreen.get(), highlightBlue.get());
        // todo hotbar.setColor(highlightRed.get(), highlightGreen.get(), highlightBlue.get());

        if (Config.show_settings && !paletteSettings.onScreen(width, height)) {
            paletteSettings.open();
        }

        if (!Config.match_textures && !colorSettings.onScreen(width, height)) {
            colorSettings.open();
        }

        showSettings.setListener(value -> {
            Config.show_settings = value;
            if (value && !paletteSettings.onScreen(width, height)) {
                paletteSettings.open();
            } else if (!value && paletteSettings.onScreen(width, height)) {
                paletteSettings.close();
            }
        });

        matchMode.setListener(value -> {
            Config.match_textures = value;
            refresh.run();
            if (Config.match_textures) {
                if (colorSettings.onScreen(width, height)) {
                    colorSettings.close();
                }
            } else {
                colorSettings.open();
            }
        });

        pickMode.setListener(value -> {
            Config.pick_mode = value;
            if (isCreativeOverlay) {
                GuiContainerCreative creative = new GuiContainerCreative(minecraft.player);
                minecraft.displayGuiScreen(creative);
            }
        });

        holdKey.setListener(value -> Config.hold_key = value);

        tooltips.setListener(value -> Config.show_tooltips = value);

        Value.Listener<Integer> colorListener = value -> {
            main.getPalette().setHighlightColor(highlightRed.get(), highlightGreen.get(), highlightBlue.get());
            // todo hotbar.setColor(highlightRed.get(), highlightGreen.get(), highlightBlue.get());
            Config.highlight_red = highlightRed.get();
            Config.highlight_green = highlightGreen.get();
            Config.highlight_blue = highlightBlue.get();
        };

        highlightRed.setListener(colorListener);
        highlightGreen.setListener(colorListener);
        highlightBlue.setListener(colorListener);

        highlightScale.setListener(value -> {
            main.getPalette().setHighlightRadius(highlightScale.get());
            Config.highlight_scale = value;
        });

        colorOpacity.setListener(value -> Config.color_opacity = value);

        colorMode.setListener(value -> {
            Config.color_mode = value;
            refresh.run();
        });

        colorAngle.setListener(value -> {
            Config.angle = value;
            refresh.run();
        });

        colorGroupSize.setListener(value -> {
            Config.group_size = value;
            refresh.run();
        });

        colorLeniency.setListener(value -> {
            Config.leniency = value;
            refresh.run();
        });

        grayPoint.setListener(value -> {
            Config.gray_point = value;
            refresh.run();
        });

        alphaPoint.setListener(value -> {
            Config.alpha_point = value;
            refresh.run();
        });
    }
}
