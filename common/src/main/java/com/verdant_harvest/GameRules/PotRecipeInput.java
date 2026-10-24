package com.verdant_harvest.GameRules;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/**
 * 锅的配方输入：3×3 网格（9 格）。燃料槽与结果槽不参与配方匹配。
 */
public record PotRecipeInput(List<ItemStack> inputs) implements RecipeInput {

    /** 锅网格边长（3×3） */
    public static final int GRID_SIZE = 3;

    @Override
    public ItemStack getItem(int index) {
        return this.inputs.get(index);
    }

    @Override
    public int size() {
        return this.inputs.size();
    }
}
