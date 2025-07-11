package com.wynntils.models.abilitytree.type;

// The state isnt required here but I left it incase  someone wanted the dumper feature back for getting the other types of nodes aswell
public record AbilityTreeNode(AbilityTreeNodeState state, int slot, int page) {}
