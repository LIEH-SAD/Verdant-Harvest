package com.verdant_harvest.Items;

import com.verdant_harvest.Blocks.Blocks;
import com.verdant_harvest.platform.Platform;
import com.verdant_harvest.platform.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

import static com.verdant_harvest.Init.LOGGER;
import static com.verdant_harvest.Init.MOD_ID;

public final class Items {
    //注册内容
    public static final RegistryEntry<Item> CORN = registerItem("corn", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build()));
    public static final RegistryEntry<Item> ROAST_CORN = registerItem("roast_corn", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build()));
    public static final RegistryEntry<Item> CORN_KERNEL = registerItem("corn_kernel", createBlockItemWithCustomItemName(Blocks.CORN), new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build()));
    public static final RegistryEntry<Item> POPCORN = registerItem("popcorn", Item::new, new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1F).build()));

    //注册方法
    private static RegistryEntry<Item> registerItem(final String name, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));

        // Item 的构造器同样会把自身绑定到 ITEM 注册表，创建必须延后。
        return Platform.get().register(BuiltInRegistries.ITEM, key, () -> {
            Item item = itemFactory.apply(properties.setId(key));
            if (item instanceof BlockItem blockItem) {
                blockItem.registerBlocks(Item.BY_BLOCK, item);
            }
            return item;
        });
    }

    private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(final RegistryEntry<Block> block) {
        return (p) -> new BlockItem(block.get(), p.useItemDescriptionPrefix());
    }

    public static void init() {
        LOGGER.info("Items have registering in {} ms", System.currentTimeMillis());
    }
}
