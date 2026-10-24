package com.verdant_harvest.BlockEntity;

import com.verdant_harvest.Blocks.Blocks;
import com.verdant_harvest.platform.Platform;
import com.verdant_harvest.platform.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

import static com.verdant_harvest.Init.id;

public class BlockEntityTypes {
    // BlockEntityType 的构造器也会绑定注册表，因此同样延迟创建；
    // 它依赖的方块在 BLOCK 注册表之后才会被注册，NeoForge 的注册表顺序保证了这个前提。
    public static final RegistryEntry<BlockEntityType<PotBlockEntity>> POT_BLOCK_ENTITY =
            Platform.get().register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("pot"),
                    () -> new BlockEntityType<>(PotBlockEntity::new, Set.of(Blocks.POT.get())));

    public static void init(){
    }
}
