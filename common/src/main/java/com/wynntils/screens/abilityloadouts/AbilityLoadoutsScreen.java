package com.wynntils.screens.abilityloadouts;

import com.wynntils.screens.base.WynntilsGridLayoutScreen;
import com.wynntils.screens.skillpointloadouts.widgets.LoadoutWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class AbilityLoadoutsScreen extends WynntilsGridLayoutScreen {

    private List<LoadoutWidget> loadoutWidgets = new ArrayList<>();

    private AbilityLoadoutsScreen() {
        super(Component.literal("Ability Loadouts Screen"));
    }

    public static Screen create() {
        return new AbilityLoadoutsScreen();
    }

    @Override
    protected void doInit() {
        super.doInit();

    }

    @Override
    public void doRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.doRender(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean doMouseClicked(double mouseX, double mouseY, int button) {
        for (LoadoutWidget widget : loadoutWidgets) {
            if (widget.isMouseOver(mouseX, mouseY)) {
                widget.mouseClicked(mouseX, mouseY, button);
                return true;
            }
        }
        return super.doMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
