package com.wynntils.screens.abilityloadouts.widgets;

import com.wynntils.core.components.Models;
import com.wynntils.screens.abilityloadouts.AbilityLoadoutsScreen;
import com.wynntils.screens.base.widgets.WynntilsButton;
import net.minecraft.network.chat.Component;

public class AbilityLoadoutDeleteButton extends WynntilsButton {
    private final AbilityLoadoutsScreen parent;

    public AbilityLoadoutDeleteButton(int x, int y, int width, int height, Component message, AbilityLoadoutsScreen parent) {
        super(x, y, width, height, message);
        this.parent = parent;
    }

    @Override
    public void onPress() {
        if (parent.getSelectedLoadout() != null) {
            Models.AbilityTree.deleteLoadout(parent.getSelectedLoadout().key());
            parent.setSelectedLoadout(null);
            parent.populateLoadouts();
            parent.doScroll(0);
        }
    }
} 