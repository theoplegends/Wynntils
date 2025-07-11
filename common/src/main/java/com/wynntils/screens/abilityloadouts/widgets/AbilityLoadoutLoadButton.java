package com.wynntils.screens.abilityloadouts.widgets;

import com.wynntils.core.components.Models;
import com.wynntils.models.abilitytree.AbilityTreeContainerQueries;
import com.wynntils.screens.abilityloadouts.AbilityLoadoutsScreen;
import com.wynntils.screens.base.widgets.WynntilsButton;
import net.minecraft.network.chat.Component;

public class AbilityLoadoutLoadButton extends WynntilsButton {
    private final AbilityLoadoutsScreen parent;

    public AbilityLoadoutLoadButton(int x, int y, int width, int height, Component message, AbilityLoadoutsScreen parent) {
        super(x, y, width, height, message);
        this.parent = parent;
    }

    @Override
    public void onPress() {
        if (parent.getSelectedLoadout() != null) {
            Models.AbilityTree.loadLoadout(parent.getSelectedLoadout().key(), (abilityList) -> {
                Models.AbilityTree.ABILITY_TREE_CONTAINER_QUERIES.unlockAbilities(abilityList);
            });
        }
    }
}