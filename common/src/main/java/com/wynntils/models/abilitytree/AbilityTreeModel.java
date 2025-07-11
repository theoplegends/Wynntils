/*
 * Copyright © Wynntils 2023-2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.abilitytree;

import com.wynntils.core.components.Model;
import com.wynntils.core.persisted.Persisted;
import com.wynntils.core.persisted.storage.Storage;
import com.wynntils.models.abilitytree.type.AbilityTreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AbilityTreeModel extends Model {

    public static final AbilityTreeContainerQueries ABILITY_TREE_CONTAINER_QUERIES = new AbilityTreeContainerQueries();

    public static class AbilityLoadout {
        public final List<AbilityTreeNode> unlockedNodes;
        public AbilityLoadout(List<AbilityTreeNode> unlockedNodes) {
            this.unlockedNodes = unlockedNodes;
        }
    }
    @Persisted
    private final Storage<Map<String, AbilityLoadout>> abilityTreeLoadouts = new Storage<>(new HashMap<>());

    public AbilityTreeModel() {
        super(List.of());
    }

    public Map<String, AbilityLoadout> getLoadouts() {
        return abilityTreeLoadouts.get();
    }

    public boolean hasLoadout(String name) {
        return abilityTreeLoadouts.get().containsKey(name);
    }

    public void deleteLoadout(String name) {
        abilityTreeLoadouts.get().remove(name);
    }

    public void loadLoadout(String name, java.util.function.Consumer<List<AbilityTreeNode>> callback) {
        AbilityLoadout loadout = abilityTreeLoadouts.get().get(name);
        if (loadout == null) return;
        callback.accept(loadout.unlockedNodes);
    }
}
