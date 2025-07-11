package com.wynntils.features.utilities;

import com.wynntils.core.components.Models;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.persisted.config.Category;
import com.wynntils.core.persisted.config.ConfigCategory;
import com.wynntils.mc.event.ScreenOpenedEvent;
import com.wynntils.models.containers.containers.AbilityTreeContainer;
import com.wynntils.screens.base.widgets.WynntilsButton;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;

@ConfigCategory(Category.UTILITIES)
public class AbilityLoadoutsFeature extends Feature {
    @SubscribeEvent
    public void onCharacterInfoScreenOpened(ScreenOpenedEvent.Post e) {
        if (!(e.getScreen() instanceof ContainerScreen screen)) return;
        if (!(Models.Container.getCurrentContainer() instanceof AbilityTreeContainer)) return;

        screen.addRenderableWidget(
            new AbilityLoadoutsFeature.LoadoutScreenButton(screen.width / 2 - AbilityLoadoutsFeature.LoadoutScreenButton.BUTTON_WIDTH / 2, screen.topPos - 24));
    }

    private static final class LoadoutScreenButton extends WynntilsButton {
        private static final int BUTTON_WIDTH = 150;
        private static final int BUTTON_HEIGHT = 20;

        private LoadoutScreenButton(int x, int y) {
            super(
                    x,
                    y,
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    Component.translatable("feature.wynntils.abilityLoadouts.button"));
        }

        @Override
        public void onPress() {
            com.wynntils.core.components.Models.AbilityTree.ABILITY_TREE_CONTAINER_QUERIES.updateParsedAbilityTree(tree -> {
                com.wynntils.core.components.Managers.TickScheduler.scheduleLater(() -> {
                    com.wynntils.utils.mc.McUtils.mc().setScreen(com.wynntils.screens.abilityloadouts.AbilityLoadoutsScreen.create());
                }, 2); // 2 ticks after entering the ability tree GUI
            });
        }
    }
}
