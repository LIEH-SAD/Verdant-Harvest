package com.verdant_harvest.platform;

/**
 * 保存当前运行时的加载器实现。
 *
 * <p>各加载器入口点会在 {@code Init.init()} / {@code InitClient.init()} 之前
 * 通过 {@link #set(IPlatform)} / {@link #setClient(IClientPlatform)} 安装自己的实现。
 */
public final class Platform {

    private static IPlatform platform;
    private static IClientPlatform clientPlatform;

    private Platform() {
    }

    /** 安装加载器实现，由各加载器的主入口点调用。 */
    public static void set(IPlatform implementation) {
        platform = implementation;
    }

    /** 安装客户端加载器实现，由各加载器的客户端入口点调用。 */
    public static void setClient(IClientPlatform implementation) {
        clientPlatform = implementation;
    }

    public static IPlatform get() {
        IPlatform current = platform;
        if (current == null) {
            throw new IllegalStateException(
                    "Platform implementation not installed yet - the loader entrypoint must call Platform.set(...) first");
        }
        return current;
    }

    public static IClientPlatform getClient() {
        IClientPlatform current = clientPlatform;
        if (current == null) {
            throw new IllegalStateException(
                    "Client platform implementation not installed yet - the client entrypoint must call Platform.setClient(...) first");
        }
        return current;
    }
}
