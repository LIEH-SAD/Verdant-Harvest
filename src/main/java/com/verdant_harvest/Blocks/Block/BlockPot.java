package com.verdant_harvest.Blocks.Block;

import com.mojang.serialization.MapCodec;
import com.verdant_harvest.BlockEntity.BlockEntityTypes;
import com.verdant_harvest.BlockEntity.PotBlockEntity;
import com.verdant_harvest.Client.Menu.PotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * 锅（Pot）方块：右键在服务端通过 {@code player.openMenu(...)} 打开 {@link PotMenu}，
 * 方块实体驱动烧制进度；被玩家破坏时掉落容器内物品。
 */
public class BlockPot extends BaseEntityBlock {
    public static final MapCodec<BlockPot> CODEC = simpleCodec(BlockPot::new);

    public MapCodec<? extends BlockPot> codec() {
        return CODEC;
    }

    public BlockPot(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.openMenu(state.getMenuProvider(level, pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityTypes.POT_BLOCK_ENTITY.get(), PotBlockEntity::tick);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof PotBlockEntity potBlockEntity) {
            potBlockEntity.dropContents(level, pos);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
