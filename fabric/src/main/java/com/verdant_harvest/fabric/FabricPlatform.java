package com.verdant_harvest.fabric;

import com.verdant_harvest.platform.CreativeTabOutput;
import com.verdant_harvest.platform.IPlatform;
import com.verdant_harvest.platform.MenuFactory;
import com.verdant_harvest.platform.RegistryEntry;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Fabric 侧的通用加载器服务实现。
 *
 * <p>Fabric 的注册表没有“冻结窗口”，对象可以立刻创建并注册。
 */
public class FabricPlatform implements IPlatform {

    @Override
    public <V, T extends V> RegistryEntry<T> register(Registry<V> registry, ResourceKey<V> key, Supplier<T> factory) {
        T value = factory.get();
        Registry.register(registry, key, value);
        return new RegistryEntry<>(key, () -> value);
    }

    @Override
    public <V, T extends V> T registerInstance(Registry<V> registry, ResourceKey<V> key, T value) {
        return Registry.register(registry, key, value);
    }

    @Override
    public CreativeModeTab createCreativeModeTab(Component title, Supplier<ItemStack> icon,
            Consumer<CreativeTabOutput> displayItems) {
        // Fabric 要求用 FabricCreativeModeTab.builder()（row 为 null、column 为 -1），
        // 并由 Fabric API 的 classtweaker 放开 CreativeModeTab.Output。
        return FabricCreativeModeTab.builder()
                .title(title)
                .icon(icon)
                .displayItems((parameters, output) -> displayItems.accept(item -> output.accept(item)))
                .build();
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        // Fabric API 的 classtweaker 放开了 MenuType 的私有构造器与 MenuSupplier。
        return new MenuType<>(factory::create, FeatureFlags.DEFAULT_FLAGS);
    }
}
