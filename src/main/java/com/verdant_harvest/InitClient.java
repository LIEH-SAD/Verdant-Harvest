package com.verdant_harvest;

import com.verdant_harvest.platform.Platform;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.verdant_harvest.Init.LOGGER;

/**
 * 共享的客户端初始化流程。
 *
 * <p>真正的客户端专用注册（界面等）由各加载器的 {@code IClientPlatform} 实现完成，
 * 因此本类不会引用任何客户端类，服务端也可以安全加载。
 */
public class InitClient {
    public InitClient() {
    }

    public static void init() {
        LOGGER.info("Hello Verdant Harvest!");
        long startTime = System.currentTimeMillis();
        Map<String, Runnable> initSteps = new LinkedHashMap();
        initSteps.put("Packet", (Runnable)() -> {});
        initSteps.put("MenuScreens", () -> Platform.getClient().registerMenuScreens());
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
