package com.verdant_harvest.GameRules;

import com.verdant_harvest.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;

import static com.verdant_harvest.Init.id;

public class GameRuleTypes {
    public static void init(){
        Platform.get().registerInstance(BuiltInRegistries.RECIPE_SERIALIZER, id("pot_cooking"), PotCookingRecipe.SERIALIZER);
    }
}
