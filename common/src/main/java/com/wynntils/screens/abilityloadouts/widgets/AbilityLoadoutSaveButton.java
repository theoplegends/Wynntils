package com.wynntils.screens.abilityloadouts.widgets;

import com.wynntils.core.components.Models;
import com.wynntils.models.abilitytree.AbilityTreeModel;
import com.wynntils.models.abilitytree.type.AbilityTreeNode;
import com.wynntils.screens.abilityloadouts.AbilityLoadoutsScreen;
import com.wynntils.screens.base.widgets.WynntilsButton;
import com.wynntils.utils.type.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class AbilityLoadoutSaveButton extends WynntilsButton {
    private final Component originalMessage;
    private final AbilityLoadoutsScreen parent;
    private boolean buttonConfirm = false;

    public AbilityLoadoutSaveButton(
            int x,
            int y,
            int width,
            int height,
            Component message,
            AbilityLoadoutsScreen parent) {
        super(x, y, width, height, message);
        this.active = false;
        this.originalMessage = message;
        this.parent = parent;
    }

    @Override
    public void onPress() {
        String name = parent.saveNameInput.getTextBoxInput();
        if (Models.AbilityTree.hasLoadout(name) && !buttonConfirm) {
            parent.hasSaveNameConflict = true;
            buttonConfirm = true;
            this.setMessage(Component.translatable("Confirm").withStyle(ChatFormatting.RED));
        } else {
            Models.AbilityTree.ABILITY_TREE_CONTAINER_QUERIES.saveAbilityTree((List<AbilityTreeNode> nodes) -> {
                Models.AbilityTree.getLoadouts().put(name, new AbilityTreeModel.AbilityLoadout(nodes));
                System.out.println("[AbilityLoadoutSaveButton] Saved loadout: " + name);
                parent.populateLoadouts();
                parent.setSelectedLoadout(Pair.of(name, Models.AbilityTree.getLoadouts().get(name)));
                parent.saveNameInput.setTextBoxInput("");
                parent.hasSaveNameConflict = false;
                reset();
            });
        }
    }

    public void reset() {
        buttonConfirm = false;
        this.setMessage(originalMessage);
    }
} 