package com.verdant_harvest.platform;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * 与 {@code MenuType.MenuSupplier} 对应的工厂接口。
 *
 * <p>原版的 {@code MenuType.MenuSupplier} 与 {@code MenuType} 的构造器都是包级私有 /
 * 私有的，只有加载器才会将其放开，因此共享代码通过本接口间接创建菜单类型。
 */
@FunctionalInterface
public interface MenuFactory<T extends AbstractContainerMenu> {

    T create(int containerId, Inventory playerInventory);
}
