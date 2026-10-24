package com.verdant_harvest.Client.Menu;

import com.verdant_harvest.Blocks.Blocks;
import com.verdant_harvest.MenuTypes;
//? if >=26.3 {
/*import net.minecraft.core.component.DataComponents;
*///?}
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * 锅菜单：3×3 配方输入格(0-8) + 燃料槽(9) + 结果槽(10)，共 47 槽（含玩家背包 27 + 热键栏 9）。
 * 燃料槽完全复用熔炉燃料判定，仅允许放入可燃物。
 */
public class PotMenu extends AbstractContainerMenu {

    public static final int GRID_SLOT_START = 0;
    public static final int GRID_SLOT_COUNT = 9;
    public static final int FUEL_SLOT = 9;
    public static final int RESULT_SLOT = 10;
    public static final int CONTAINER_SLOT_COUNT = 11;
    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL = 3;
    public static final int DATA_COUNT = 4;

    private static final int PLAYER_SLOT_START = CONTAINER_SLOT_COUNT;
    private static final int SLOT_COUNT = PLAYER_SLOT_START + 36;

    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final Level level;

    /** 客户端/服务端无方块实体时使用的默认容器 */
    public PotMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory,
                new SimpleContainer(CONTAINER_SLOT_COUNT),
                new SimpleContainerData(DATA_COUNT),
                ContainerLevelAccess.NULL);
    }

    public PotMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(MenuTypes.POT_MENU, containerId);
        this.container = container;
        this.data = data;
        this.access = access;
        this.level = playerInventory.player.level();

        // 顶部 3×3 配方输入格：x = 30 + col*18，y = 17 + row*18
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(container, row * 3 + col, 30 + col * 18, 17 + row * 18));
            }
        }
        // 燃料槽 (89, 53)：仅允许放入熔炉燃料或空桶
        this.addSlot(new Slot(container, FUEL_SLOT, 89, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return PotMenu.this.isFuel(stack) || stack.is(Items.BUCKET);
            }

            @Override
            public int getMaxStackSize(ItemStack stack) {
                return stack.is(Items.BUCKET) ? 1 : super.getMaxStackSize(stack);
            }
        });
        // 大结果槽 (124, 35)，仅机器产出，玩家不可放入
        this.addSlot(new Slot(container, RESULT_SLOT, 124, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // 背包 27（y 84/102/120）+ 热键栏 9（y 142）
        this.addStandardInventorySlots(playerInventory, 8, 84);

        this.addDataSlots(data);
    }

    // 使用熔炉同款燃料数据判断是否为燃料。
    public boolean isFuel(ItemStack stack) {
        //? if >=26.3 {
        /*return stack.has(DataComponents.COOKING_FUEL);
        *///?} else {
        return this.level.fuelValues().isFuel(stack);
        //?}
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < CONTAINER_SLOT_COUNT) {
                // 容器槽 → 玩家背包/热键栏
                if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, SLOT_COUNT, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.isFuel(stack) || stack.is(Items.BUCKET)) {
                // 燃料 → 燃料槽；燃料槽已满则退回网格
                if (!this.moveItemStackTo(stack, FUEL_SLOT, FUEL_SLOT + 1, false)) {
                    if (!this.moveItemStackTo(stack, GRID_SLOT_START, GRID_SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // 食材 → 网格（前 9 格）
                if (!this.moveItemStackTo(stack, GRID_SLOT_START, GRID_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, Blocks.POT.get());
    }

    /** 烧制进度 0..1（用于箭头） */
    public float getCookProgress() {
        int total = this.data.get(DATA_COOKING_TOTAL);
        if (total <= 0) {
            return 0.0F;
        }
        return Math.min(1.0F, (float) this.data.get(DATA_COOKING_PROGRESS) / total);
    }

    /** 燃料燃烧进度 0..1（用于火焰） */
    public float getLitProgress() {
        int duration = this.data.get(DATA_LIT_DURATION);
        if (duration <= 0) {
            return 0.0F;
        }
        return Math.min(1.0F, (float) this.data.get(DATA_LIT_TIME) / duration);
    }

    /** 是否正在燃烧（用于火焰） */
    public boolean isLit() {
        return this.data.get(DATA_LIT_TIME) > 0;
    }

    /** 是否正在烧制 */
    public boolean isCooking() {
        return this.data.get(DATA_COOKING_PROGRESS) > 0;
    }
}
