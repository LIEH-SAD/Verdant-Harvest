package com.verdant_harvest.Blocks;

import com.verdant_harvest.Blocks.Block.BlockCorn;
import com.verdant_harvest.Blocks.Block.BlockPot;
import com.verdant_harvest.Blocks.Block.BlockTomato;
import com.verdant_harvest.platform.RegistryEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.verdant_harvest.mapping.IBlock.register;

public class Blocks {
    // 方块本身要等到注册时才被创建（见 IPlatform#register），所以这里持有的是句柄。
    public static final RegistryEntry<Block> CORN = register("corn_block", BlockCorn::new, BlockBehaviour.Properties.of().noCollision().sound(SoundType.CROP).randomTicks().instabreak(), false);
    public static final RegistryEntry<Block> TOMATO = register("tomato_block", BlockTomato::new, BlockBehaviour.Properties.of().noCollision().sound(SoundType.CROP).randomTicks().instabreak(), false);
    public static final RegistryEntry<Block> POT = register("pot", BlockPot::new, BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.STONE), true);

    public static void init(){
    }
}
