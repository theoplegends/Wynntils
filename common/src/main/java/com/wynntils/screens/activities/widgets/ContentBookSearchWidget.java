/*
 * Copyright © Wynntils 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.screens.activities.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.screens.base.TextboxScreen;
import com.wynntils.screens.base.widgets.SearchWidget;
import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.render.Texture;
import java.util.function.Consumer;

public class ContentBookSearchWidget extends SearchWidget {
    public ContentBookSearchWidget(
            int x, int y, int width, int height, Consumer<String> onUpdateConsumer, TextboxScreen textboxScreen) {
        super(x, y, width, height, onUpdateConsumer, textboxScreen);
    }

    @Override
    protected void renderBackground(PoseStack poseStack) {
        RenderUtils.drawTexturedRect(poseStack, Texture.CONTENT_BOOK_SEARCH, getX() - 15, getY());
    }
}
