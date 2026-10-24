package com.verdant_harvest.fabric;

//? if fabric {
import com.verdant_harvest.Client.Screen.PotScreen;
import com.verdant_harvest.MenuTypes;
import com.verdant_harvest.platform.IClientPlatform;
import net.minecraft.client.gui.screens.MenuScreens;

/**
 * Fabric 侧的客户端加载器服务实现。
 *
 * <p>只会由 {@link VerdantHarvestFabricClient} 加载，因此可以安全地引用客户端类。
 */
public class FabricClientPlatform implements IClientPlatform {

    @Override
    public void registerMenuScreens() {
        MenuScreens.register(MenuTypes.POT_MENU, PotScreen::new);
    }
}
//?}
