package com.wynntils.screens.abilityloadouts.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.abilitytree.AbilityTreeModel.AbilityLoadout;
import com.wynntils.models.abilitytree.type.AbilityTreeNode;
import com.wynntils.models.character.type.ClassType;
import com.wynntils.screens.abilityloadouts.AbilityLoadoutsScreen;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.render.FontRenderer;
import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.TextShadow;
import com.wynntils.utils.render.type.VerticalAlignment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class AbilityLoadoutWidget extends AbstractWidget {
    private final float dividedWidth;
    private final String name;
    private final AbilityLoadout loadout;
    private final AbilityLoadoutsScreen parent;

    public AbilityLoadoutWidget(
            int x,
            int y,
            int width,
            int height,
            float dividedWidth,
            String name,
            AbilityLoadout loadout,
            AbilityLoadoutsScreen parent) {
        super(x, y, width, height, Component.literal(name));
        this.dividedWidth = dividedWidth;
        this.name = name;
        this.loadout = loadout;
        this.parent = parent;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();

        if (this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawRect(
                    poseStack, CommonColors.GRAY.withAlpha(100), this.getX(), this.getY(), 0, width, height);
        }
        if (parent.getSelectedLoadout() != null
                && parent.getSelectedLoadout().key().equals(this.name)) {
            RenderUtils.drawRectBorders(
                    poseStack,
                    CommonColors.WHITE,
                    this.getX(),
                    this.getY(),
                    this.getX() + this.getWidth(),
                    this.getY() + this.getHeight(),
                    1,
                    0.5f);
        }

        FontRenderer.getInstance()
                .renderText(
                        poseStack,
                        StyledText.fromString(name),
                        dividedWidth * 4,
                        this.getY() + (float) this.getHeight() / 2,
                        CommonColors.WHITE,
                        HorizontalAlignment.LEFT,
                        VerticalAlignment.MIDDLE,
                        TextShadow.NORMAL);
        FontRenderer.getInstance()
                .renderText(
                        poseStack,
                        StyledText.fromString("Abilities: " + getUnlockedNodeCount()),
                        dividedWidth * 20,
                        this.getY() + (float) this.getHeight() / 2,
                        CommonColors.WHITE,
                        HorizontalAlignment.LEFT,
                        VerticalAlignment.MIDDLE,
                        TextShadow.NORMAL);
    }

    private int getUnlockedNodeCount() {
        return loadout.unlockedNodes.size();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        parent.setSelectedLoadout(com.wynntils.utils.type.Pair.of(name, loadout));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
} 