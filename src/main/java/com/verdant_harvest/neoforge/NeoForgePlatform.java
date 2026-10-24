package com.verdant_harvest.neoforge;

//? if neoforge {
/*import com.verdant_harvest.Init;
import com.verdant_harvest.platform.CreativeTabOutput;
import com.verdant_harvest.platform.IPlatform;
import com.verdant_harvest.platform.MenuFactory;
import com.verdant_harvest.platform.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

// NeoForge 侧的通用加载器服务实现。
//
// NeoForge 在 mod 构造期间冻结了所有内置注册表，而且 Block/Item/BlockEntityType 的构造器
// 会立刻把自己绑定进注册表，所以不仅注册、连对象的创建都必须推迟到 RegisterEvent：
// 这里统一登记到 DeferredRegister，由 attachRegistrations() 挂到 mod 事件总线。
public class NeoForgePlatform implements IPlatform {

    private final IEventBus modEventBus;
    private final Map<Registry<?>, DeferredRegister<?>> deferredRegisters = new LinkedHashMap<>();

    public NeoForgePlatform(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }

    @Override
    public <V, T extends V> RegistryEntry<T> register(Registry<V> registry, ResourceKey<V> key, Supplier<T> factory) {
        DeferredHolder<V, T> holder = deferredRegister(registry).<T>register(key.identifier().getPath(), factory);
        return new RegistryEntry<>(key, holder);
    }

    @Override
    public <V, T extends V> T registerInstance(Registry<V> registry, ResourceKey<V> key, T value) {
        deferredRegister(registry).register(key.identifier().getPath(), () -> value);
        return value;
    }

    @SuppressWarnings("unchecked")
    private <V> DeferredRegister<V> deferredRegister(Registry<V> registry) {
        return (DeferredRegister<V>) this.deferredRegisters.computeIfAbsent(registry,
                target -> DeferredRegister.create((Registry<V>) target, Init.MOD_ID));
    }

    // 把收集到的所有 DeferredRegister 挂到 mod 事件总线上，NeoForge 随后才会执行真正的注册。
    public void attachRegistrations() {
        this.deferredRegisters.values().forEach(register -> register.register(this.modEventBus));
    }

    @Override
    public CreativeModeTab createCreativeModeTab(Component title, Supplier<ItemStack> icon,
            Consumer<CreativeTabOutput> displayItems) {
        // NeoForge 向 CreativeModeTab 补丁了无参 builder()，它会按 mod id 自动分配 row/column；
        // CreativeModeTab.Output 则由 NeoForge 的 access transformer 放开。
        return CreativeModeTab.builder()
                .title(title)
                .icon(icon)
                .displayItems((parameters, output) -> displayItems.accept(item -> output.accept(item)))
                .build();
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        // NeoForge 的 access transformer 放开了 MenuType 的私有构造器与 MenuSupplier。
        return new MenuType<>(factory::create, FeatureFlags.DEFAULT_FLAGS);
    }
}
*///?}
