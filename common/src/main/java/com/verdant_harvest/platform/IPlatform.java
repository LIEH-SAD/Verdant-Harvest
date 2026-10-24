package com.verdant_harvest.platform;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 由各加载器实现的服务接口：共享代码需要、但两个加载器的实现方式不同的部分。
 *
 * <p>实现类由各加载器入口点在 {@code Init.init()} 之前通过 {@link Platform#set(IPlatform)} 安装。
 *
 * <p>这里的签名刻意避开原版中受访问控制的成员（{@code CreativeModeTab.Output}、
 * {@code MenuType.MenuSupplier} 及其私有构造器），这些成员只有加载器才会放开，
 * 共享模块是直接针对未修改的原版编译的。
 */
public interface IPlatform {

    /**
     * 延迟创建并注册一个对象，返回其句柄。
     *
     * <p>必须用这个方法的类型：{@code Block}、{@code Item}、{@code BlockEntityType}。
     * 它们的构造器会把自己绑定进内置注册表，而 NeoForge 在 mod 构造期间冻结了注册表，
     * 所以只能把 {@code factory} 推迟到 RegisterEvent 执行。
     */
    <V, T extends V> RegistryEntry<T> register(Registry<V> registry, ResourceKey<V> key, Supplier<T> factory);

    /**
     * 注册一个已经创建好的实例，并原样返回它。
     *
     * <p>适用于构造时不接触注册表的类型：{@code MenuType}、{@code CreativeModeTab}、
     * {@code RecipeType}、{@code RecipeSerializer}。注意 NeoForge 上注册动作本身仍然是延后的，
     * 只是对象已经存在而已。
     */
    <V, T extends V> T registerInstance(Registry<V> registry, ResourceKey<V> key, T value);

    /** 便捷重载：直接使用 {@link Identifier}。 */
    default <V, T extends V> RegistryEntry<T> register(Registry<V> registry, Identifier id, Supplier<T> factory) {
        return register(registry, ResourceKey.create(registry.key(), id), factory);
    }

    /** 便捷重载：直接使用 {@link Identifier}。 */
    default <V, T extends V> T registerInstance(Registry<V> registry, Identifier id, T value) {
        return registerInstance(registry, ResourceKey.create(registry.key(), id), value);
    }

    /**
     * 构建一个自定义创造模式物品栏。
     *
     * <p>Fabric 与 NeoForge 构造物品栏的方式不同：Fabric 必须使用
     * {@code FabricCreativeModeTab.builder()}（row 为 {@code null}、column 为 {@code -1}），
     * 而 NeoForge 向 {@code CreativeModeTab} 补丁了一个无参的 {@code builder()}。
     */
    CreativeModeTab createCreativeModeTab(Component title, Supplier<ItemStack> icon,
            Consumer<CreativeTabOutput> displayItems);

    /**
     * 构建一个 {@link MenuType}。
     *
     * <p>原版把构造器与 {@code MenuSupplier} 都设为私有，Fabric 通过 classtweaker、
     * NeoForge 通过 access transformer 将其放开，因此只有加载器模块才能调用。
     */
    <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory);
}
