package com.verdant_harvest.platform;

import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/**
 * 注册结果的句柄。
 *
 * <p>Fabric 上对象在注册那一刻就存在，{@link #get()} 立刻可用；NeoForge 上对象的
 * <em>创建</em>本身也必须推迟到 RegisterEvent（{@code Block}/{@code Item}/
 * {@code BlockEntityType} 的构造器会直接把自己绑定进注册表，注册表冻结时构造就会抛错），
 * 因此共享代码只能持有本句柄，等到运行时再取值。
 */
public final class RegistryEntry<T> implements Supplier<T> {

    // 键的注册表类型与取值的具体类型未必一致
    // （例如 BLOCK_ENTITY_TYPE 的键是 ResourceKey<BlockEntityType<?>>，取值却是 BlockEntityType<PotBlockEntity>），
    // 所以这里诚实地标成 ResourceKey<?>。
    private final ResourceKey<?> key;
    private final Supplier<T> value;

    public RegistryEntry(ResourceKey<?> key, Supplier<T> value) {
        this.key = key;
        this.value = value;
    }

    public ResourceKey<?> key() {
        return this.key;
    }

    @Override
    public T get() {
        return this.value.get();
    }

    @Override
    public String toString() {
        return "RegistryEntry[" + this.key + "]";
    }
}
