package com.niuhi.config;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class DynamicListController<K, V> implements Controller<KeyValueController.KeyValuePair<K, V>> {
    private final Option<KeyValueController.KeyValuePair<K, V>> option;
    private final double ratio; private final Controller keyController;
    private final Controller valueController;

    public DynamicListController(Option<KeyValueController.KeyValuePair<K, V>> option, double ratio, Controller<K> keyController, Controller<V> valueController) {
        this.option = option;
        this.ratio = ratio;
        this.keyController = keyController;
        this.valueController = valueController;
    }

    @Override
    public Option<KeyValueController.KeyValuePair<K, V>> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        KeyValueController.KeyValuePair<K, V> pair = option.pendingValue();
        return Text.literal(pair.getKey() + "|" + pair.getValue());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new DynamicListControllerElement(this, screen, widgetDimension);
    }

    public class DynamicListControllerElement extends ControllerWidget<DynamicListController<K, V>> {
        private final AbstractWidget keyElement;
        private final AbstractWidget valueElement;

        public DynamicListControllerElement(DynamicListController<K, V> control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
            Dimension<Integer> keyDimension = dim.withWidth((int)(dim.width() * control.ratio));
            Dimension<Integer> valueDimension = dim.moved(keyDimension.width(), 0).withWidth(dim.width() - keyDimension.width());
            this.keyElement = keyController.provideWidget(screen, keyDimension);
            this.valueElement = valueController.provideWidget(screen, valueDimension);
        }

        @Override
        protected void drawHoveredControl(DrawContext graphics, int mouseX, int mouseY, float delta) {
            // No-op: Handled by keyElement and valueElement
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
            keyElement.render(graphics, mouseX, mouseY, delta);
            valueElement.render(graphics, mouseX, mouseY, delta);
        }

        @Override
        public void setDimension(Dimension<Integer> dim) {
            Dimension<Integer> keyDimension = dim.withWidth((int)(dim.width() * ratio));
            Dimension<Integer> valueDimension = dim.moved(keyDimension.width(), 0).withWidth(dim.width() - keyDimension.width());
            keyElement.setDimension(keyDimension);
            valueElement.setDimension(valueDimension);
            super.setDimension(dim);
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            keyElement.mouseMoved(mouseX, mouseY);
            valueElement.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return keyElement.mouseClicked(mouseX, mouseY, button) || valueElement.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return keyElement.mouseReleased(mouseX, mouseY, button) || valueElement.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return keyElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || valueElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            return keyElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount) || valueElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return keyElement.keyPressed(keyCode, scanCode, modifiers) || valueElement.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return keyElement.keyReleased(keyCode, scanCode, modifiers) || valueElement.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return keyElement.charTyped(chr, modifiers) || valueElement.charTyped(chr, modifiers);
        }

        @Override
        public void unfocus() {
            keyElement.unfocus();
            valueElement.unfocus();
            super.unfocus();
        }
    }

}