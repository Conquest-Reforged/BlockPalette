package me.dags.blockpalette.gui;

import me.dags.blockpalette.util.Pointer;
import me.dags.blockpalette.util.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dags <dags@dags.me>
 */
public class UI {

    static final Label DUMMY = new Label("", 0xFFFFFF);

    public static class Label extends GuiButton {

        private final int color;

        public Label(String text, int color) {
            super(0, 0, 0, text);
            this.color = color;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            FontRenderer renderer = mc.fontRendererObj;
            renderer.drawStringWithShadow(displayString, xPosition, yPosition + 10, color);
        }
    }

    public static class AreaCycler<T> extends Cycler<T> {

        private final ResourceLocation texture;

        public AreaCycler(Pointer<T> value, T[] options, ResourceLocation texture) {
            super(value, options, "");
            this.texture = texture;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;

            if (hovered) {
                Render.cleanup();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                Render.drawTexture(texture, xPosition, yPosition, width, height, 0, 0, width, height);
            }
        }
    }

    public static class Button extends GuiButton {

        private final Runnable action;

        public Button(String display, Runnable action) {
            super(0, 0, 0, display);
            this.action = action;
        }

        @Override
        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            boolean pressed = super.mousePressed(minecraft, mouseX, mouseY);
            if (pressed) {
                super.playPressSound(minecraft.getSoundHandler());
                action.run();
            }
            return pressed;
        }
    }

    public static class Cycler<T> extends GuiButton {

        private final Pointer<T> value;
        private final String format;
        private final T[] options;

        private int pos = 0;

        public Cycler(Pointer<T> value, T[] options) {
            this(value, options, "%s");
        }

        public Cycler(Pointer<T> value, T[] options, String format) {
            super(0, 0, 0, String.format(format, value.get()));
            this.options = options;
            this.format = format;
            this.value = value;

            T defaultValue = value.get();

            for (int i = 0; i < options.length; i++) {
                T val = options[i];
                if (val.equals(defaultValue)) {
                    pos = i;
                    return;
                }
            }
        }

        @Override
        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            boolean pressed = super.mousePressed(minecraft, mouseX, mouseY);
            if (pressed) {
                super.playPressSound(minecraft.getSoundHandler());

                pos = pos + 1 < options.length ? pos + 1 : 0;
                if (pos < options.length) {
                    value.set(options[pos]);
                    this.displayString = String.format(format, value.get());
                }
            }
            return pressed;
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            super.mouseReleased(mouseX, mouseY);
            value.markUpdated();
        }
    }

    public static class FloatSlider extends GuiSlider {

        private final Pointer<Float> value;

        public FloatSlider(String name, float min, float max, Pointer<Float> value) {
            super(floatResponder(value), 0, 0, 0, name, min, max, value.get(), floatFormat());
            this.value = value;
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            super.mouseReleased(mouseX, mouseY);
            value.markUpdated();
        }
    }

    public static class IntSlider extends GuiSlider {

        private final Pointer<Integer> value;

        public IntSlider(String name, float min, float max, Pointer<Integer> value) {
            super(intResponder(value), 0, 0, 0, name, min, max, value.get(), intFormat());
            this.value = value;
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            super.mouseReleased(mouseX, mouseY);
            value.markUpdated();
        }
    }

    private static GuiSlider.FormatHelper floatFormat() {
        return new GuiSlider.FormatHelper() {
            @Override
            public String getText(int id, String name, float value) {
                return String.format("%s: %.2f", name, value);
            }
        };
    }

    private static GuiSlider.FormatHelper intFormat() {
        return new GuiSlider.FormatHelper() {
            @Override
            public String getText(int id, String name, float value) {
                return String.format("%s: %.0f", name, value);
            }
        };
    }

    private static GuiPageButtonList.GuiResponder floatResponder(final Pointer<Float> pointer) {
        return new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {
            }

            @Override
            public void setEntryValue(int id, float value) {
                pointer.set(value);
            }

            @Override
            public void setEntryValue(int id, String value) {
            }
        };
    }

    private static GuiPageButtonList.GuiResponder intResponder(final Pointer<Integer> pointer) {
        return new GuiPageButtonList.GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {
            }

            @Override
            public void setEntryValue(int id, float value) {
                pointer.set(Math.round(value));
            }

            @Override
            public void setEntryValue(int id, String value) {
            }
        };
    }
}
