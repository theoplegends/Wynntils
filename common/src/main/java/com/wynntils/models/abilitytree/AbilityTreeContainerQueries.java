/*
 * Copyright © Wynntils 2023-2024.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.abilitytree;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Managers;
import com.wynntils.core.components.Models;
import com.wynntils.handlers.container.scriptedquery.QueryStep;
import com.wynntils.handlers.container.scriptedquery.ScriptedContainerQuery;
import com.wynntils.handlers.container.type.ContainerContent;
import com.wynntils.models.abilitytree.type.AbilityTreeNode;
import com.wynntils.models.abilitytree.type.AbilityTreeNodeState;
import com.wynntils.models.containers.ContainerModel;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.wynn.ContainerUtils;
import com.wynntils.utils.wynn.InventoryUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.component.CustomModelData;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AbilityTreeContainerQueries {
    private static final int ABILITY_TREE_SLOT = 9;
    private static final int PREVIOUS_PAGE_SLOT = 57;
    private static final int NEXT_PAGE_SLOT = 59;
    private static final int MAX_PAGE_COUNT = 7;

    // In order of color from left to right: green, white, yellow, red, blue, pink
    private final float[] UNLOCKABLE_CUSTOMDATA = {51f, 19f, 23f, 35f, 31f, 27f};
    private final float[] SELECTED_CUSTOMDATA = {52f, 20f, 24f, 36f, 32f, 28f};

    private int pageCount;

    // Callback needed so the ability tree menu can open properly
    public void saveAbilityTree(Consumer<List<AbilityTreeNode>> callback) {
        ContainerUtils.closeBackgroundContainer();

        Managers.TickScheduler.scheduleNextTick(() -> queryAbilityTree(
                new AbilityTreeProcessor() {
                    private final List<AbilityTreeNode> unlockedNodes = new ArrayList<>();

                    @Override
                    protected void processPage(ContainerContent content, int page) {
                        java.util.List<ItemStack> items = content.items();
                        for (int slot = 0; slot < items.size(); slot++) {
                            ItemStack itemStack = items.get(slot);
                            if (!(itemStack.getItem() instanceof PotionItem)) continue;
                            if (isSelectedNode(itemStack)) {
                                unlockedNodes.add(new AbilityTreeNode(AbilityTreeNodeState.UNLOCKED, slot, page));
                            }
                        }
                        if (page == MAX_PAGE_COUNT) {
                            callback.accept(unlockedNodes);
                        }
                    }

                    private boolean isSelectedNode(ItemStack itemStack) {
                        CustomModelData cmd = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
                        if (cmd == null || cmd.floats().isEmpty()) return false;
                        float customModelData = cmd.floats().get(0);
                        for (float value : SELECTED_CUSTOMDATA) {
                            if (customModelData == value) return true;
                        }
                        return false;
                    }
                }
        ));
    }

    public void loadAbilityTree(List<AbilityTreeNode> nodes) {
        ContainerUtils.closeBackgroundContainer();

        Managers.TickScheduler.scheduleNextTick(() -> queryAbilityTree(
                new AbilityTreeProcessor() {
                    @Override
                    protected void processPage(ContainerContent content, int page) {
                        boolean unlockedAny;
                        do {
                            unlockedAny = false;

                            List<ItemStack> items = content.items();
                            for (int slot = 0; slot < items.size(); slot++) {
                                ItemStack itemStack = items.get(slot);
                                if (!(itemStack.getItem() instanceof PotionItem)) continue;

                                if (isUnlockableNode(itemStack, slot, page)) {
                                    // Unlock the node
//                                    System.out.println(String.format("slot:%d, page:%d", slot, page));
//                                    QueryStep.clickOnSlot(slot);

//                                    ContainerUtils.clickOnSlot(slot, content.containerId(), GLFW.GLFW_MOUSE_BUTTON_LEFT, content.items());
//


//                                    Int2ObjectMap<ItemStack> changedSlots = new Int2ObjectOpenHashMap<>();
//                                    changedSlots.put(slot, new ItemStack(Items.AIR));
//                                    McUtils.sendPacket(new ServerboundContainerClickPacket(content.containerId(), McUtils.inventoryMenu().getStateId(), slot, 0, ClickType.PICKUP, ItemStack.EMPTY, changedSlots));


//                                    McUtils.mc().gameMode.handleInventoryMouseClick(0, slot, 0, ClickType.PICKUP, McUtils.player());

                                    System.out.println("Found matching item at slot:" + slot + " page:" + page);

                                    unlockedAny = false;
                                    break; // break early to force a container refresh and get the new state
                                }
                            }
                        } while (unlockedAny);
                    }

                    private boolean isUnlockableNode(ItemStack itemStack, int slot, int page) {
                        // Check if the node (slot, page) is in the target list
                        boolean matchesTarget = nodes.stream().anyMatch(n -> n.slot() == slot && n.page() == page);
                        if (!matchesTarget) return false;

                        // Check if the item has unlockable CMD
                        CustomModelData cmd = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
                        if (cmd == null || cmd.floats().isEmpty()) return false;
                        float customModelData = cmd.floats().get(0);
                        for (float value : UNLOCKABLE_CUSTOMDATA) {
                            if (customModelData == value) return true;
                        }
                        return false;
                    }
                }
        ));
    }

    public void queryAbilityTree(AbilityTreeProcessor processor) {
        ScriptedContainerQuery query = ScriptedContainerQuery.builder("Ability Tree Query")
                .onError(msg -> {
                    WynntilsMod.warn("Problem querying Ability Tree: " + msg);
                    McUtils.sendErrorToClient("Dumping Ability Tree failed");
                })

                // Open character/compass menu
                .then(QueryStep.useItemInHotbar(InventoryUtils.COMPASS_SLOT_NUM)
                        .expectContainerTitle(ContainerModel.CHARACTER_INFO_NAME))

                // Open ability menu
                .then(QueryStep.clickOnSlot(ABILITY_TREE_SLOT)
                        .expectContainerTitle(Models.Container.ABILITY_TREE_PATTERN.pattern()))

                // Go to first page, and save current page number
                .execute(() -> this.pageCount = 0)
                .repeat(
                        c -> {
                            ItemStack item = c.items().get(PREVIOUS_PAGE_SLOT);
                            return item.getItem() == Items.POTION;
                        },
                        QueryStep.clickOnSlot(PREVIOUS_PAGE_SLOT).processIncomingContainer(c -> {
                            // Count how many times this is done, and save this value.
                            // If we did not even enter here, we were already on first page.
                            this.pageCount++;
                        }))

                // Process first page
                .reprocess(processor::processPage)

                // Repeatedly go to next page, if any, and process it
                .repeat(
                        c -> {
                            ItemStack item = c.items().get(NEXT_PAGE_SLOT);
                            if (item.getItem() != Items.POTION) return false;
                            String name = item.getHoverName().getString();
                            CustomModelData cmd = item.get(DataComponents.CUSTOM_MODEL_DATA);
                            int customModelData = (cmd != null && !cmd.floats().isEmpty()) ? Math.round(cmd.floats().get(0)) : -1;
                            return name.equals("§7Next Page") && customModelData == 10;
                        },
                        QueryStep.clickOnSlot(NEXT_PAGE_SLOT).processIncomingContainer(processor::processPage))

                //process last page
                .reprocess(processor::processPage)

                // Go back to initial page
                .repeat(
                        c -> {
                            // Go back as many pages as the original count was from the end
                            this.pageCount++;
                            return this.pageCount != MAX_PAGE_COUNT;
                        },
                        QueryStep.clickOnSlot(PREVIOUS_PAGE_SLOT))
                .build();

        query.executeQuery();
    }

    public abstract static class AbilityTreeProcessor {
        private int page = 1;

        protected void processPage(ContainerContent content) {
            processPage(content, page);
            page++;
        }

        protected abstract void processPage(ContainerContent content, int page);
    }
}