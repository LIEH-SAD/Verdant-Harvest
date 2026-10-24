package com.verdant_harvest.GameRules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verdant_harvest.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

import static com.verdant_harvest.Init.id;

/**
 * 锅烧制配方：使用合成台式 3×3 形状图案（pattern + key）精确匹配锅的网格，
 * 烧制 {@code cookingtime} 刻并消耗燃料后产出 {@code result}。
 */
public class PotCookingRecipe implements Recipe<PotRecipeInput> {

    /** 配方类型（注册到 RECIPE_TYPE，id 与序列化器一致） */
    public static final RecipeType<PotCookingRecipe> TYPE = Platform.get().registerInstance(
            BuiltInRegistries.RECIPE_TYPE, id("pot_cooking"), new RecipeType<>() {
            });

    public static final MapCodec<PotCookingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(PotCookingRecipe::group),
            ShapedRecipePattern.MAP_CODEC.forGetter(PotCookingRecipe::pattern),
            Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(PotCookingRecipe::cookingTime),
            ItemStack.CODEC.fieldOf("result").forGetter(PotCookingRecipe::result)
    ).apply(instance, PotCookingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PotCookingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PotCookingRecipe::group,
            ShapedRecipePattern.STREAM_CODEC, PotCookingRecipe::pattern,
            ByteBufCodecs.VAR_INT, PotCookingRecipe::cookingTime,
            ItemStack.STREAM_CODEC, PotCookingRecipe::result,
            PotCookingRecipe::new
    );

    public static final RecipeSerializer<PotCookingRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private static final RecipeBookCategory RECIPE_BOOK_CATEGORY = new RecipeBookCategory();

    private final String group;
    private final ShapedRecipePattern pattern;
    private final int cookingTime;
    private final ItemStack result;

    public PotCookingRecipe(String group, ShapedRecipePattern pattern, int cookingTime, ItemStack result) {
        this.group = group;
        this.pattern = pattern;
        this.cookingTime = cookingTime;
        this.result = result;
    }

    @Override
    public boolean matches(PotRecipeInput input, Level level) {
        return this.findPlacement(input) >= 0;
    }

    /**
     * 在 3×3 网格中查找图案匹配的左上角槽位索引；无匹配返回 -1。
     */
    public int findPlacement(PotRecipeInput input) {
        int maxOffsetX = PotRecipeInput.GRID_SIZE - this.pattern.width();
        int maxOffsetY = PotRecipeInput.GRID_SIZE - this.pattern.height();
        if (maxOffsetX < 0 || maxOffsetY < 0) {
            return -1;
        }
        for (int offsetY = 0; offsetY <= maxOffsetY; offsetY++) {
            for (int offsetX = 0; offsetX <= maxOffsetX; offsetX++) {
                if (this.matchesAt(input, offsetX, offsetY)) {
                    return offsetY * PotRecipeInput.GRID_SIZE + offsetX;
                }
            }
        }
        return -1;
    }

    private boolean matchesAt(PotRecipeInput input, int offsetX, int offsetY) {
        List<Optional<Ingredient>> ingredients = this.pattern.ingredients();
        for (int y = 0; y < this.pattern.height(); y++) {
            for (int x = 0; x < this.pattern.width(); x++) {
                Optional<Ingredient> ingredient = ingredients.get(y * this.pattern.width() + x);
                ItemStack stack = input.getItem((offsetX + x) + (offsetY + y) * PotRecipeInput.GRID_SIZE);
                if (!Ingredient.testOptionalIngredient(ingredient, stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(PotRecipeInput input) {
        return this.result.copy();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public RecipeSerializer<? extends Recipe<PotRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<PotRecipeInput>> getType() {
        return TYPE;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RECIPE_BOOK_CATEGORY;
    }

    public ShapedRecipePattern pattern() {
        return this.pattern;
    }

    public int cookingTime() {
        return this.cookingTime;
    }

    public ItemStack result() {
        return this.result;
    }
}
