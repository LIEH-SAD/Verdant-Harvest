package com.verdant_harvest.mapping;

import com.verdant_harvest.platform.Platform;
import com.verdant_harvest.platform.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

import static com.verdant_harvest.Init.MOD_ID;

public class IBlock {
    public static RegistryEntry<Block> register(final String name, final Function<BlockBehaviour.Properties, Block> factory, final BlockBehaviour.Properties properties, boolean shouldRegisterItem) {
        ResourceKey<Block> id = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, name));

        // Block 的构造器会把自身绑定到 BLOCK 注册表，因此创建必须延后到真正注册的时刻。
        RegistryEntry<Block> block = Platform.get().register(BuiltInRegistries.BLOCK, id,
                () -> factory.apply(properties.setId(id)));

        if (shouldRegisterItem) {
            registerBlockItem(name, block);
        }

        return block;
    }

    static void registerBlockItem(String name, RegistryEntry<Block> block) {
        ResourceKey<Item> id = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        Platform.get().register(BuiltInRegistries.ITEM, id,
                () -> new net.minecraft.world.item.BlockItem(block.get(), new Item.Properties().setId(id).useBlockDescriptionPrefix()));
    }

    public static RegistryEntry<Block> register(final String name, final BlockBehaviour.Properties properties, boolean shouldRegisterItem) {
        return register(name, Block::new, properties, shouldRegisterItem);
    }
    public static void init(){}
}
