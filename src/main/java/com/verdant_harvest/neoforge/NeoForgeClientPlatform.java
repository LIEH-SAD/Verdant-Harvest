package com.verdant_harvest.neoforge;

//? if neoforge {
/*import com.verdant_harvest.Client.Screen.PotScreen;
import com.verdant_harvest.MenuTypes;
import com.verdant_harvest.platform.IClientPlatform;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

// NeoForge 侧的客户端加载器服务实现。
//
// NeoForge 的界面注册不走 MenuScreens.register，而是在 RegisterMenuScreensEvent 中完成，
// 因此这里把监听器挂到 mod 事件总线上，由事件触发真正的注册。
public class NeoForgeClientPlatform implements IClientPlatform {

    private final IEventBus modEventBus;

    public NeoForgeClientPlatform(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }

    @Override
    public void registerMenuScreens() {
        this.modEventBus.addListener((RegisterMenuScreensEvent event) ->
                event.register(MenuTypes.POT_MENU, PotScreen::new));
    }
}
*///?}
