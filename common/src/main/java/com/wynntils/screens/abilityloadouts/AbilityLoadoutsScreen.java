package com.wynntils.screens.abilityloadouts;

import com.wynntils.core.components.Models;
import com.wynntils.models.abilitytree.AbilityTreeModel.AbilityLoadout;
import com.wynntils.models.abilitytree.type.AbilityTreeNode;
import com.wynntils.screens.abilityloadouts.widgets.*;
import com.wynntils.screens.base.WynntilsGridLayoutScreen;
import com.wynntils.screens.base.widgets.TextInputBoxWidget;
import com.wynntils.utils.type.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class AbilityLoadoutsScreen extends WynntilsGridLayoutScreen {

    private List<AbstractWidget> loadoutWidgets = new ArrayList<>();
    private Pair<String, AbilityLoadout> selectedLoadout;
    public TextInputBoxWidget saveNameInput;
    private AbilityLoadoutSaveButton saveButton;
    private AbilityLoadoutLoadButton loadButton;
    private AbilityLoadoutDeleteButton deleteButton;
    private AbilityLoadoutScrollBar scrollBar;
    private float scrollPercent = 0;
    private static final int MAX_LOADOUTS_PER_PAGE = 11;
    public boolean hasSaveNameConflict = false;

    private AbilityLoadoutsScreen() {
        super(Component.literal("Ability Loadouts Screen"));
    }

    public static Screen create() {
        return new AbilityLoadoutsScreen();
    }

    @Override
    protected void doInit() {
        super.doInit();
        populateLoadouts();

        saveNameInput = new TextInputBoxWidget(
                (int) (dividedWidth * 35),
                (int) (dividedHeight * 24),
                (int) ((dividedWidth * 48) - (dividedWidth * 35)),
                BUTTON_SIZE,
                (x) -> saveButton.active = !x.isBlank(),
                this,
                saveNameInput);
        this.addRenderableWidget(saveNameInput);

        saveButton = new AbilityLoadoutSaveButton(
                (int) (dividedWidth * 49),
                (int) (dividedHeight * 24),
                (int) ((dividedWidth * 53) - (dividedWidth * 49)),
                BUTTON_SIZE,
                Component.translatable("Save"),
                this);
        this.addRenderableWidget(saveButton);

        loadButton = new AbilityLoadoutLoadButton(
                (int) (dividedWidth * 35),
                (int) (dividedHeight * 52),
                (int) ((dividedWidth * 44) - (dividedWidth * 35)),
                BUTTON_SIZE,
                Component.translatable("Load"),
                this);
        this.addRenderableWidget(loadButton);

        deleteButton = new AbilityLoadoutDeleteButton(
                (int) (dividedWidth * 45),
                (int) (dividedHeight * 52),
                (int) ((dividedWidth * 51) - (dividedWidth * 45)),
                BUTTON_SIZE,
                Component.translatable("Delete").withStyle(ChatFormatting.RED),
                this);
        this.addRenderableWidget(deleteButton);

        scrollBar = new AbilityLoadoutScrollBar(dividedWidth * 30, dividedHeight * 8, dividedWidth * 0.5f, 0, this, dividedHeight);
        this.addRenderableWidget(scrollBar);

        setSelectedLoadout(null);
    }

    @Override
    public void doRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.doRender(guiGraphics, mouseX, mouseY, partialTick);
        var poseStack = guiGraphics.pose();

        // Wider border around loadout list area
        com.wynntils.utils.render.RenderUtils.drawRectBorders(
            poseStack,
            com.wynntils.utils.colors.CommonColors.WHITE,
            dividedWidth * 2.5f,
            dividedHeight * 8,
            dividedWidth * 31.5f,
            dividedHeight * 56,
            0,
            1);

        // Border around save area (name input + save button)
        com.wynntils.utils.render.RenderUtils.drawRectBorders(
            poseStack,
            com.wynntils.utils.colors.CommonColors.WHITE,
            dividedWidth * 34.5f,
            dividedHeight * 18.5f,
            dividedWidth * 53.5f,
            dividedHeight * 28.5f,
            1,
            1);

        // Border around selected loadout area (if any)
        if (selectedLoadout != null) {
            com.wynntils.utils.render.RenderUtils.drawRectBorders(
                poseStack,
                com.wynntils.utils.colors.CommonColors.WHITE,
                dividedWidth * 34,
                dividedHeight * 34,
                dividedWidth * 60,
                dividedHeight * 56,
                1,
                1);
        }

        // Selected loadout name above load button
        if (selectedLoadout != null) {
            com.wynntils.utils.render.FontRenderer.getInstance().renderText(
                poseStack,
                com.wynntils.core.text.StyledText.fromString(selectedLoadout.key()),
                dividedWidth * 39.5f,
                dividedHeight * 50.5f,
                com.wynntils.utils.colors.CommonColors.WHITE,
                com.wynntils.utils.render.type.HorizontalAlignment.CENTER,
                com.wynntils.utils.render.type.VerticalAlignment.BOTTOM,
                com.wynntils.utils.render.type.TextShadow.NORMAL);
        }

        for (AbstractWidget widget : loadoutWidgets) {
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean doMouseClicked(double mouseX, double mouseY, int button) {
        for (AbstractWidget widget : loadoutWidgets) {
            if (widget.isMouseOver(mouseX, mouseY)) {
                widget.mouseClicked(mouseX, mouseY, button);
                return true;
            }
        }
        return super.doMouseClicked(mouseX, mouseY, button);
    }

    public void doScroll(double scrollAmount) {
        int scrollableWidgets = Math.max(0, loadoutWidgets.size() - MAX_LOADOUTS_PER_PAGE);
        if (scrollableWidgets == 0) return;
        float scrollableRatio = (float) scrollableWidgets / loadoutWidgets.size();
        float maxScrollOffset = (4 * (loadoutWidgets.size() - 1) - 43) / scrollableRatio;
        scrollPercent = (float) Math.max(0, Math.min(scrollableRatio, scrollPercent - scrollAmount / 50));

        for (int i = 0; i < loadoutWidgets.size(); i++) {
            AbstractWidget widget = loadoutWidgets.get(i);
            float baseYPosition = dividedHeight * (9f + i * 4f);
            float scrollOffset = dividedHeight * maxScrollOffset * scrollPercent;
            widget.setY((int) (baseYPosition - scrollOffset));
            widget.visible = !(widget.getY() <= dividedHeight * 4) && !(widget.getY() >= dividedHeight * 56);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (loadoutWidgets.size() <= MAX_LOADOUTS_PER_PAGE) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        doScroll(scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 261 && deleteButton.active) { // GLFW.GLFW_KEY_DELETE
            deleteButton.onPress();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setSelectedLoadout(Pair<String, AbilityLoadout> loadout) {
        selectedLoadout = loadout;
        boolean hasSelection = selectedLoadout != null;
        if (loadButton != null) {
            loadButton.visible = hasSelection;
            loadButton.active = hasSelection;
        }
        if (deleteButton != null) {
            deleteButton.visible = hasSelection;
            deleteButton.active = hasSelection;
        }
    }

    public Pair<String, AbilityLoadout> getSelectedLoadout() {
        return selectedLoadout;
    }

    public void populateLoadouts() {
        loadoutWidgets = new ArrayList<>();
        var loadouts = Models.AbilityTree.getLoadouts();
        System.out.println("[AbilityLoadoutsScreen] Populating loadouts: " + loadouts);
        for (var entry : loadouts.entrySet()) {
            System.out.println("[AbilityLoadoutsScreen] Loadout '" + entry.getKey() + "' unlocked nodes: " + entry.getValue().unlockedNodes);
            loadoutWidgets.add(new AbilityLoadoutWidget(
                    (int) (dividedWidth * 3.5f),
                    (int) (dividedHeight * (9 + loadoutWidgets.size() * 4)),
                    (int) (dividedWidth * 27),
                    (int) (dividedHeight * 4),
                    dividedWidth,
                    entry.getKey(),
                    entry.getValue(),
                    this));
        }
        setSelectedLoadout(null);
    }
}
