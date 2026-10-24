package com.verdant_harvest.platform;

import net.minecraft.world.level.ItemLike;

/**
 * 与 {@code CreativeModeTab.Output} 对应、但不暴露原版受保护类型的最小接口。
 *
 * <p>原版的 {@code CreativeModeTab.Output} 是 {@code protected}，只有加载器（Fabric 的
 * classtweaker / NeoForge 的 access transformer）才会将其放开。共享代码因此改用本接口，
 * 由各加载器在 {@code displayItems} 回调中转发。
 */
@FunctionalInterface
public interface CreativeTabOutput {

    void accept(ItemLike item);
}
