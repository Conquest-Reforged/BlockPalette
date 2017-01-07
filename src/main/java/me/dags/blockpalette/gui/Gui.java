package me.dags.blockpalette.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;

/**
 * @author dags <dags@dags.me>
 */
public class Gui {

    public static class ActionButton extends GuiButton {

        private static int id = 0;
        private final Action action;
        private boolean pressed = false;

        public ActionButton(String buttonText, Action action) {
            super(id--, 0, 0, buttonText);
            this.action = action;
        }

        @Override
        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            return pressed = super.mousePressed(minecraft, mouseX, mouseY);
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            super.mouseReleased(mouseX, mouseY);
            if (pressed) {
                pressed = false;
                onAction();
            }
        }

        void onAction() {
            action.onAction(this);
        }
    }

    public static class ActionSlider extends GuiSlider {

        private static int id = 0;
        private final Action action;

        private boolean pressed = false;
        private float currentValue = 0F;

        public ActionSlider(float min, float max, float defaultValue, FormatHelper formatter, Action action) {
            super(dummy, id++, 0, 0, "", min, max, defaultValue, formatter);
            this.action = action;
            this.currentValue = defaultValue;
        }

        @Override
        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            return pressed = super.mousePressed(minecraft, mouseX, mouseY);
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            super.mouseReleased(mouseX, mouseY);
            if (pressed && getSliderValue() != currentValue) {
                currentValue = getSliderValue();
                action.onAction(this);
            }
            pressed = false;
        }
    }

    public interface Action {
        void onAction(GuiButton button);
    }

    private static final GuiPageButtonList.GuiResponder dummy = new GuiPageButtonList.GuiResponder() {
        public void setEntryValue(int id, boolean value) {}
        public void setEntryValue(int id, float value) {}
        public void setEntryValue(int id, String value) {}
    };
}
