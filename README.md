# Verdant Harvest

Minecraft **26.2** 的 NeoForge + Fabric 双端模组（"A timely snow promises a good harvest."）。

## 项目结构

```
verdant-harvest/
├── common/     共享代码：所有内容定义与逻辑，只依赖原版 API
├── fabric/     Fabric 入口点 + fabric.mod.json + Fabric 侧平台实现 + datagen
└── neoforge/   NeoForge 入口点 + neoforge.mods.toml + NeoForge 侧平台实现 + datagen
```

`common` 通过 Loom 直接针对**未混淆的原版**编译（26.x 已取消混淆，因此既不需要 `mappings`，
也不需要 `modImplementation`）。`fabric` / `neoforge` 各自的 `jar` 任务会把 `common` 编译产物
与资源一起打进成品 jar，所以每个加载器得到的都是一个自包含的模组。

## 版本

| 组件 | 版本 |
| --- | --- |
| Minecraft | 26.2 |
| Java | 25 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.161.0+26.2 |
| Fabric Loom | 1.17.20 |
| NeoForge | 26.2.0.88 |
| ModDevGradle | 2.0.148 |
| Gradle | 9.7.0 |

版本统一写在根目录 `gradle.properties`。Gradle 守护进程通过 `gradle/gradle-daemon-jvm.properties`
要求 Java 25，缺失时由 foojay resolver 自动下载。

## 常用命令

```bash
./gradlew build              # 构建两个加载器的 jar
./gradlew :fabric:runClient  # Fabric 客户端
./gradlew :fabric:runDatagen # Fabric 数据生成
./gradlew :neoforge:runClient
./gradlew :neoforge:runServer
./gradlew :neoforge:runData
```

产物位于 `fabric/build/libs/` 与 `neoforge/build/libs/`。

## 平台差异如何处理

`common` 里不允许出现任何加载器 API。两个加载器真正不一致的地方集中在
`common/src/main/java/com/verdant_harvest/platform/`，由各加载器入口点在
`Init.init()` 之前安装自己的实现：

| 接口 | 差异 | Fabric | NeoForge |
| --- | --- | --- | --- |
| `IPlatform#register` | 对象创建时机 | 立即创建并 `Registry.register` | 交给 `DeferredRegister`，在 RegisterEvent 中创建 |
| `IPlatform#registerInstance` | 已存在对象的注册时机 | 立即注册 | 延迟到 RegisterEvent |
| `IPlatform#createCreativeModeTab` | 物品栏构建方式 | `FabricCreativeModeTab.builder()`（row=null, column=-1） | 无参 `CreativeModeTab.builder()`（NeoForge 补丁） |
| `IPlatform#createMenuType` | `MenuType` 构造器可见性 | 由 Fabric API 的 classtweaker 放开 | 由 NeoForge 的 access transformer 放开 |
| `IClientPlatform#registerMenuScreens` | 界面注册入口 | `MenuScreens.register` | `RegisterMenuScreensEvent` |

### 为什么是"延迟创建"而不只是"延迟注册"

26.2 里 `Block`、`Item`、`BlockEntityType` 的**构造器**会调用
`Registry#createIntrusiveHolder` 把自己绑定进注册表；而 NeoForge 在 mod 构造期间冻结了
内置注册表。所以这两个类型必须先登记工厂、等到 `RegisterEvent` 才创建，共享代码因此持有
`RegistryEntry<T>` 句柄而不是实例本身（`Blocks.POT.get()`）。

`MenuType` / `CreativeModeTab` / `RecipeType` / `RecipeSerializer` 构造时不接触注册表，
可以立刻创建，只需延迟注册（`registerInstance`）。

`Block`/`Item` 之间的依赖（如 BlockItem 需要 Block）由原版注册表顺序保证：
`Block` → `Item` → `BlockEntityType` → `MenuType` → `RecipeType` → `RecipeSerializer`。

## 已验证

- `./gradlew build` 成功产出 `verdant-harvest-fabric-0.0.1.jar` 与 `verdant-harvest-neoforge-0.0.1.jar`
- `:neoforge:runData` 与 `:fabric:runDatagen` 均能启动并完成全部 9 步注册

## 许可

MIT
