package com.verdant_harvest.BlockEntity;

import com.verdant_harvest.Client.Menu.PotMenu;
import com.verdant_harvest.GameRules.PotCookingRecipe;
import com.verdant_harvest.GameRules.PotRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
//? if >=26.3 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.phys.Vec3;
*///?} else {
import net.minecraft.world.level.block.entity.FuelValues;
//?}
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 锅方块实体：持有 11 格容器（3×3 网格 0-8、燃料槽 9、结果槽 10），
 * 燃料机制完全复用熔炉的燃料数据（同一种燃料、同样的燃烧时长），
 * 每个服务器 tick 匹配 {@link PotCookingRecipe} 并消耗燃料推进烧制进度。
 */
public class PotBlockEntity extends BlockEntity implements Container, MenuProvider {

    public static final int GRID_SLOT_START = 0;
    public static final int GRID_SLOT_COUNT = 9;
    public static final int FUEL_SLOT = 9;
    public static final int RESULT_SLOT = 10;
    public static final int CONTAINER_SIZE = 11;
    public static final int DATA_LIT_TIME = 0;
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL = 3;
    public static final int DATA_COUNT = 4;

    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private int litTimeRemaining;
    private int litTotalTime;
    private int cookingProgress;
    private int cookingTotalTime;

    private final ContainerData dataAccess = new SimpleContainerData(DATA_COUNT) {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_LIT_TIME -> PotBlockEntity.this.litTimeRemaining;
                case DATA_LIT_DURATION -> PotBlockEntity.this.litTotalTime;
                case DATA_COOKING_PROGRESS -> PotBlockEntity.this.cookingProgress;
                case DATA_COOKING_TOTAL -> PotBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_LIT_TIME -> PotBlockEntity.this.litTimeRemaining = value;
                case DATA_LIT_DURATION -> PotBlockEntity.this.litTotalTime = value;
                case DATA_COOKING_PROGRESS -> PotBlockEntity.this.cookingProgress = value;
                case DATA_COOKING_TOTAL -> PotBlockEntity.this.cookingTotalTime = value;
            }
        }
    };

    public PotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.POT_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PotBlockEntity blockEntity) {
        if (level.isClientSide()) {
            return;
        }
        boolean dirty = false;
        ItemStack fuelStack = blockEntity.items.get(FUEL_SLOT);
        PotCookingRecipe recipe = blockEntity.getCurrentRecipe().map(RecipeHolder::value).orElse(null);
        boolean canCraft = recipe != null && blockEntity.canCraft(recipe);

        // 燃烧计时（即使无配方也会自然燃尽）
        boolean isLit = blockEntity.litTimeRemaining > 0;
        if (isLit) {
            blockEntity.litTimeRemaining--;
        }
        isLit = blockEntity.litTimeRemaining > 0;

        if (canCraft) {
            // 未点燃且需要烧制时，点燃新燃料
            if (!isLit) {
                //? if >=26.3 {
                /*int burnDuration = blockEntity.getBurnDuration((ServerLevel) level, fuelStack);
                *///?} else {
                int burnDuration = blockEntity.getBurnDuration(level.fuelValues(), fuelStack);
                //?}
                blockEntity.litTotalTime = burnDuration;
                blockEntity.litTimeRemaining = burnDuration;
                if (burnDuration > 0) {
                    consumeFuel(blockEntity.items, fuelStack);
                    isLit = true;
                    dirty = true;
                }
            }
            if (isLit) {
                blockEntity.cookingTotalTime = recipe.cookingTime();
                blockEntity.cookingProgress++;
                if (blockEntity.cookingProgress >= blockEntity.cookingTotalTime) {
                    blockEntity.cookingProgress = 0;
                    blockEntity.craft(recipe);
                    dirty = true;
                }
            }
        } else if (recipe == null) {
            blockEntity.cookingProgress = 0;
            blockEntity.cookingTotalTime = 0;
        }

        if (dirty) {
            blockEntity.setChanged();
        }
    }

    // 读取熔炉同款燃料数据，返回该物品的燃烧时长（刻）。
    //? if >=26.3 {
    /*protected int getBurnDuration(ServerLevel level, ItemStack stack) {
        return ResolvableInt.getFromItem(stack, DataComponents.COOKING_FUEL, CookingFuel::burnTime,
                this.getLootContext(level), 0);
    }

    private LootContext getLootContext(ServerLevel level) {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.BLOCK_STATE, this.getBlockState())
                .withParameter(LootContextParams.BLOCK_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.getBlockPos()))
                .withParameter(LootContextParams.CONTAINER, this)
                .create(LootContextParamSets.CONTAINER_PROCESS);
        return new LootContext.Builder(params).create(Optional.empty());
    }
    *///?} else {
    protected int getBurnDuration(FuelValues fuelValues, ItemStack stack) {
        return fuelValues.burnDuration(stack);
    }
    //?}

    /** 消耗一份燃料；烧完若存在合成残留（如空桶）则留在燃料槽。 */
    private static void consumeFuel(NonNullList<ItemStack> items, ItemStack fuelStack) {
        Item item = fuelStack.getItem();
        fuelStack.shrink(1);
        if (fuelStack.isEmpty()) {
            ItemStackTemplate remainder = item.getCraftingRemainder();
            items.set(FUEL_SLOT, remainder != null ? remainder.create() : ItemStack.EMPTY);
        }
    }

    private Optional<RecipeHolder<PotCookingRecipe>> getCurrentRecipe() {
        if (this.level == null || this.level.getServer() == null) {
            return Optional.empty();
        }
        return this.level.getServer().getRecipeManager().getRecipeFor(PotCookingRecipe.TYPE, this.asInput(), this.level);
    }

    private PotRecipeInput asInput() {
        List<ItemStack> inputs = new ArrayList<>(GRID_SLOT_COUNT);
        for (int i = 0; i < GRID_SLOT_COUNT; i++) {
            inputs.add(this.items.get(i));
        }
        return new PotRecipeInput(inputs);
    }

    private boolean canCraft(PotCookingRecipe recipe) {
        ItemStack result = recipe.result();
        ItemStack resultSlot = this.items.get(RESULT_SLOT);
        if (resultSlot.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(resultSlot, result)) {
            return false;
        }
        return resultSlot.getCount() + result.getCount() <= resultSlot.getMaxStackSize();
    }

    private void craft(PotCookingRecipe recipe) {
        // 定位图案在 3×3 网格中的左上角位置
        int placement = recipe.findPlacement(this.asInput());
        if (placement < 0) {
            this.cookingProgress = 0;
            return;
        }
        int offsetX = placement % PotRecipeInput.GRID_SIZE;
        int offsetY = placement / PotRecipeInput.GRID_SIZE;
        var ingredients = recipe.pattern().ingredients();
        for (int y = 0; y < recipe.pattern().height(); y++) {
            for (int x = 0; x < recipe.pattern().width(); x++) {
                if (ingredients.get(y * recipe.pattern().width() + x).isPresent()) {
                    int slot = offsetX + x + (offsetY + y) * PotRecipeInput.GRID_SIZE;
                    ItemStack stack = this.items.get(slot);
                    if (!stack.isEmpty()) {
                        stack.shrink(1);
                    }
                }
            }
        }
        ItemStack resultSlot = this.items.get(RESULT_SLOT);
        ItemStack result = recipe.result().copy();
        if (resultSlot.isEmpty()) {
            this.items.set(RESULT_SLOT, result);
        } else {
            resultSlot.grow(result.getCount());
        }
        this.setChanged();
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, this);
    }

    // ---------- Container ----------

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            this.items.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == RESULT_SLOT) {
            return false;
        }
        if (index == FUEL_SLOT) {
            if (this.level == null) {
                return false;
            }
            ItemStack current = this.items.get(FUEL_SLOT);
            //? if >=26.3 {
            /*return stack.has(DataComponents.COOKING_FUEL) || (stack.is(Items.BUCKET) && !current.is(Items.BUCKET));
            *///?} else {
            return this.level.fuelValues().isFuel(stack) || (stack.is(Items.BUCKET) && !current.is(Items.BUCKET));
            //?}
        }
        return true;
    }

    // ---------- NBT ----------

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putShort("CookingProgress", (short) this.cookingProgress);
        output.putShort("CookingTotalTime", (short) this.cookingTotalTime);
        output.putShort("LitTimeRemaining", (short) this.litTimeRemaining);
        output.putShort("LitTotalTime", (short) this.litTotalTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.cookingProgress = input.getShortOr("CookingProgress", (short) 0);
        this.cookingTotalTime = input.getShortOr("CookingTotalTime", (short) 0);
        this.litTimeRemaining = input.getShortOr("LitTimeRemaining", (short) 0);
        this.litTotalTime = input.getShortOr("LitTotalTime", (short) 0);
    }

    // ---------- MenuProvider ----------

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.verdant_harvest.pot");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PotMenu(containerId, playerInventory, this, this.dataAccess,
                ContainerLevelAccess.create(this.level, this.worldPosition));
    }
}
