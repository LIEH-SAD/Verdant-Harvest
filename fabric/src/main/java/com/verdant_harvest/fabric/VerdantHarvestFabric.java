package com.verdant_harvest.fabric;

import com.verdant_harvest.Init;
import com.verdant_harvest.platform.Platform;
import net.fabricmc.api.ModInitializer;

public class VerdantHarvestFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Platform.set(new FabricPlatform());
		Init.init();
	}
}
