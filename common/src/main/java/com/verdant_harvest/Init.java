package com.verdant_harvest;

import com.verdant_harvest.BlockEntity.BlockEntityTypes;
import com.verdant_harvest.Blocks.Blocks;
import com.verdant_harvest.GameRules.GameRuleTypes;
import com.verdant_harvest.Items.Items;
import com.verdant_harvest.ItemsGroup.ItemsGroup;
import com.verdant_harvest.ItemsGroup.ItemsGroupEvent;
import com.verdant_harvest.mapping.IBlock;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class Init {
    public static final String MOD_ID = "verdant_harvest";
    public static final Logger LOGGER = LoggerFactory.getLogger("Verdant Harvest");
    public static final String VERSION = "1.0.0";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public Init() {
    }

    public static void init() {
        LOGGER.info("Hello Verdant Harvest!");
        long startTime = System.currentTimeMillis();
        Map<String, Runnable> initSteps = new LinkedHashMap();
        initSteps.put("ItemsGroup", ItemsGroup::init);
        initSteps.put("ItemsGroupEvent", ItemsGroupEvent::init);
        initSteps.put("IBlock", IBlock::init);
        initSteps.put("Blocks", Blocks::init);
        initSteps.put("BlockEntityTypes", BlockEntityTypes::init);
        initSteps.put("GameRules", GameRuleTypes::init);
        initSteps.put("MenuTypes", MenuTypes::init);
        initSteps.put("Items", Items::init);
        initSteps.put("Packet", (Runnable)() -> {});
        int currentStep = 1;

        for(Map.Entry<String, Runnable> step : initSteps.entrySet()) {
            LOGGER.info("Verdant Harvest is registering , Please wait... {} ({}/{})", step.getKey(), currentStep, initSteps.size());
            ((Runnable)step.getValue()).run();
            ++currentStep;
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        LOGGER.info("Verdant Harvest has successfully registered in {} ms.", duration);
    }
}
