package com.verdant_harvest.platform;

/**
 * 仅客户端使用的加载器服务。
 *
 * <p>实现类只会由客户端入口点加载，因此可以安全地引用客户端类。
 * 它不继承 {@link IPlatform}：客户端入口点不应该接触注册表，避免误把注册写进
 * 一个永远不会被提交的延迟注册队列。
 */
public interface IClientPlatform {

    /** 向加载器的界面注册表登记本模组的菜单界面。 */
    void registerMenuScreens();
}
