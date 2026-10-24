package com.verdant_harvest.ItemsGroup;

import com.verdant_harvest.Blocks.Blocks;
import com.verdant_harvest.Items.Items;
import com.verdant_harvest.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static com.verdant_harvest.Init.MOD_ID;

public class ItemsGroup {
    public static final ResourceKey<CreativeModeTab> SEED_TAB = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(MOD_ID, "material"));

    public static final ResourceKey<CreativeModeTab> VERDANT_HARVEST_FOOD_TAB = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(MOD_ID, "food"));

    // 物品栏的构建方式因加载器而异（原版的 CreativeModeTab.Output 受访问控制），
    // 因此交由 IPlatform 构建（见 platform 包）。
    // 图标与内容都在回调里取句柄，等到物品栏真正展开时才需要物品已经注册完毕。
    public static final CreativeModeTab SEED = Platform.get().createCreativeModeTab(
            Component.translatable("itemGroup.verdant_harvest.seed"),
            () -> new ItemStack(Items.CORN.get()),
            output -> {
                output.accept(Items.CORN_KERNEL.get());
                output.accept(Blocks.POT.get());
            });

    public static final CreativeModeTab VERDANT_HARVEST_FOOD = Platform.get().createCreativeModeTab(
            Component.translatable("itemGroup.verdant_harvest.food"),
            () -> new ItemStack(Items.ROAST_CORN.get()),
            output -> {
                output.accept(Items.CORN.get());
                output.accept(Items.CORN_KERNEL.get());
                output.accept(Items.ROAST_CORN.get());
                output.accept(Items.POPCORN.get());
            });

    public static void init(){
        Platform.get().registerInstance(BuiltInRegistries.CREATIVE_MODE_TAB, SEED_TAB, SEED);
        Platform.get().registerInstance(BuiltInRegistries.CREATIVE_MODE_TAB, VERDANT_HARVEST_FOOD_TAB, VERDANT_HARVEST_FOOD);
    }
}
