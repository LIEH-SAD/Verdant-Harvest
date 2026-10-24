package com.verdant_harvest.neoforge;

import com.verdant_harvest.InitClient;
import com.verdant_harvest.platform.Platform;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * 客户端入口点。{@code dist = Dist.CLIENT} 表示这个类只会在客户端加载，
 * 因此其中的客户端专用代码在专用服务端上是安全的。
 */
@Mod(value = VerdantHarvestNeoForge.MOD_ID, dist = Dist.CLIENT)
public class VerdantHarvestNeoForgeClient {

    public VerdantHarvestNeoForgeClient(IEventBus modEventBus) {
        Platform.setClient(new NeoForgeClientPlatform(modEventBus));
        InitClient.init();
    }
}
