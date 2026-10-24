package com.verdant_harvest;

import com.verdant_harvest.Client.Menu.PotMenu;
import com.verdant_harvest.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

import static com.verdant_harvest.Init.id;

public class MenuTypes {
    // MenuType 的构造器在原版中是私有的，只有加载器会将其放开，
    // 因此这里交由 IPlatform 创建（见 platform 包）；它构造时不接触注册表，可以直接创建。
    public static final MenuType<PotMenu> POT_MENU = Platform.get().createMenuType(PotMenu::new);

    public static void init(){
        Platform.get().registerInstance(BuiltInRegistries.MENU, id("pot"), POT_MENU);
    }
}
