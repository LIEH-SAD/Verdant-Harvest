package com.verdant_harvest.neoforge;

//? if neoforge {
import com.verdant_harvest.Init;
import com.verdant_harvest.platform.Platform;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(VerdantHarvestNeoForge.MOD_ID)
public class VerdantHarvestNeoForge {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "verdant_harvest";

    public VerdantHarvestNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // NeoForge 的内置注册表在 mod 构造期间是冻结的，Init.init() 只会把对象登记到
        // DeferredRegister；随后 attachRegistrations() 把它们挂到 mod 事件总线，
        // 等到 RegisterEvent 触发时才真正写入注册表。
        NeoForgePlatform platform = new NeoForgePlatform(modEventBus);
        Platform.set(platform);
        Init.init();
        platform.attachRegistrations();

        // Keep the data generator entrypoint in sync with the Fabric module.
        // GatherDataEvent 本身是抽象类，必须监听具体子类；这里的 data 运行配置用的是 clientData()。
        modEventBus.addListener(this::gatherData);
    }

    private void gatherData(GatherDataEvent.Client event) {
        // 目前没有需要生成的数据提供器；这里保留入口以便后续添加。
    }
}
//?}
