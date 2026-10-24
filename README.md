# Verdant Harvest

Minecraft **26.2 / 26.3** 的 Fabric + NeoForge 双端模组（"A timely snow promises a good harvest."）。

项目使用 [Stonecutter](https://stonecutter.kikugie.dev/) 做多版本管理：同一份 `src/` 会分别按
`26.2-fabric`、`26.2-neoforge`、`26.3-fabric`、`26.3-neoforge` 四个节点编译。

## 项目结构

```
verdant-harvest/
├── src/main/java/        共享代码 + 各加载器实现（用 Stonecutter 条件区分）
├── src/main/resources/   共享资源 + fabric.mod.json + neoforge.mods.toml
├── build.fabric.gradle.kts    Fabric 节点构建脚本（Loom）
├── build.neoforge.gradle.kts  NeoForge 节点构建脚本（ModDevGradle）
├── stonecutter.gradle.kts     Stonecutter 控制器（当前激活节点、条件常量）
├── stonecutter.properties.toml  模组元数据 + 各版本/加载器的依赖版本
├── buildSrc/             NeoForge 编译互斥服务（避免多版本同时重编译 Minecraft）
└── versions/             Stonecutter 生成的节点工程（已 gitignore）
```

加载器专用的 Java 文件（`com.verdant_harvest.fabric.*` / `com.verdant_harvest.neoforge.*`）
整文件包在 `//? if fabric { ... //?}` / `//? if neoforge { ... //?}` 里；
26.2 与 26.3 之间的原版 API 差异用 `//? if <26.3 { ... //?} else { ... //?}` 处理。

`versions/` 是生成目录：`build/` 产物、IntelliJ 运行配置和 `stonecutterSwitchTo...` 任务都由
插件生成，不需要提交。

## 版本

| 组件 | 26.2 节点 | 26.3 节点 |
| --- | --- | --- |
| Minecraft | 26.2 | 26.3 |
| Fabric Loader | 0.19.5 | 0.19.5 |
| Fabric API | 0.161.0+26.2 | 0.163.0+26.3 |
| NeoForge | 26.2.0.88 | 26.3.0.86 |

公共工具链：Java 25、Fabric Loom 1.18.4（需 Gradle 9.7+）、ModDevGradle 2.0.148、
Stonecutter 0.9.8、Gradle 9.7.0。所有版本集中在 `stonecutter.properties.toml`。

Gradle 守护进程通过 `gradle/gradle-daemon-jvm.properties` 要求 Java 25；缺失时由
foojay resolver 自动下载。

## 常用命令

```bash
./gradlew build                        # 构建 4 个节点的 jar
./gradlew :26.2-fabric:runClient       # 26.2 Fabric 客户端（开发配置）
./gradlew :26.3-fabric:runClient       # 26.3 Fabric 客户端
./gradlew :26.2-neoforge:runClient     # 26.2 NeoForge 客户端
./gradlew :26.3-neoforge:runClient     # 26.3 NeoForge 客户端
./gradlew :26.3-fabric:runDatagen      # Fabric 数据生成
./gradlew :26.3-neoforge:runData       # NeoForge 数据生成
```

产物位于 `versions/<节点>/build/libs/`。所有节点共用根目录的 `run/` 作为运行目录。

在 IntelliJ IDEA 中执行一次 Gradle 同步后，Loom / ModDevGradle 会为每个节点生成
`runClient` / `runServer` / 数据生成运行配置，可直接从右上角运行。
`stonecutterSwitchTo<节点>` 运行配置用来切换 `src/` 中激活的版本/加载器状态。

## 平台差异如何处理

`src/main/java` 里的共享代码不直接引用加载器 API：两个加载器真正不一致的地方集中在
`com/verdant_harvest/platform/`，由各加载器入口点在 `Init.init()` 之前安装自己的实现。

| 接口 | 差异 | Fabric | NeoForge |
| --- | --- | --- | --- |
| `IPlatform#register` | 对象创建时机 | 立即创建并 `Registry.register` | 交给 `DeferredRegister`，在 RegisterEvent 中创建 |
| `IPlatform#registerInstance` | 已存在对象的注册时机 | 立即注册 | 延迟到 RegisterEvent |
| `IPlatform#createCreativeModeTab` | 物品栏构建方式 | `FabricCreativeModeTab.builder()` | 无参 `CreativeModeTab.builder()`（NeoForge 补丁） |
| `IPlatform#createMenuType` | `MenuType` 构造器可见性 | 由 Fabric API 的 classtweaker 放开 | 由 NeoForge 的 access transformer 放开 |
| `IClientPlatform#registerMenuScreens` | 界面注册入口 | `MenuScreens.register` | `RegisterMenuScreensEvent` |

### 为什么是"延迟创建"而不只是"延迟注册"

26.x 里 `Block`、`Item`、`BlockEntityType` 的**构造器**会调用
`Registry#createIntrusiveHolder` 把自己绑定进注册表；而 NeoForge 在 mod 构造期间冻结了
内置注册表。所以这两个类型必须先登记工厂、等到 `RegisterEvent` 才创建，共享代码因此持有
`RegistryEntry<T>` 句柄而不是实例本身（`Blocks.POT.get()`）。

`MenuType` / `CreativeModeTab` / `RecipeType` / `RecipeSerializer` 构造时不接触注册表，
可以立刻创建，只需延迟注册（`registerInstance`）。

### 26.2 → 26.3 的原版 API 变化

已用条件编译处理的部分：

- `Block` 的 `MapCodec`/`simpleCodec` 在 26.3 被移除 → 自定义方块的 `CODEC`/`codec()` 只在 `<26.3` 编译。
- `FuelValues` / `Level#fuelValues()` 在 26.3 被移除 → 26.3 改用 `DataComponents.COOKING_FUEL`
  与 `ResolvableInt.getFromItem(...)`。
- `BonemealableBlock` 的三个方法在 26.3 增加了 `BonemealSource` 参数。

## 已验证

- `./gradlew build` 成功产出 4 个 jar（`verdant_harvest-{fabric,neoforge}-0.0.1+{26.2,26.3}.jar`）。
- `:26.2-fabric:runClient`、`:26.3-fabric:runClient`、`:26.2-neoforge:runClient`、
  `:26.3-neoforge:runClient` 均能启动并进入主菜单，模组 9 步注册全部完成。

## 许可

MIT