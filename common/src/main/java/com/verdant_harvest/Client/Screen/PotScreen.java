package com.verdant_harvest.Client.Screen;

import com.verdant_harvest.Client.Menu.PotMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.verdant_harvest.Init.MOD_ID;

/**
 * 锅（Pot）GUI：渲染 176×160 面板（47 槽位、箭头、火焰均烘焙在 {@code gui_pot.png} 中），
 * 并根据菜单同步的烧制进度绘制箭头与火焰指示。
 *
 * <p>本类为客户端专用类，只会由各加载器的客户端入口点触达，因此可以安全地引用客户端类。
 */
public class PotScreen extends AbstractContainerScreen<PotMenu> {

    /** 面板底图纹理（256×256，有效面板区 176×160） */
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/gui_pot.png");

    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 160;
    private static final int TEXTURE_SIZE = 256;
    private static final int ARROW_X = 88;
    private static final int ARROW_Y = 16;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 16;
    private static final int FLAME_X = 88;
    private static final int FLAME_Y = 35;
    private static final int FLAME_WIDTH = 18;
    private static final int FLAME_HEIGHT = 22;

    public PotScreen(PotMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickDelta) {
        super.extractBackground(graphics, mouseX, mouseY, tickDelta);
        // 面板底图（含全部槽位、箭头、火焰）
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos,
                0.0F, 0.0F, PANEL_WIDTH, PANEL_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        // 烧制进度箭头（白色填充随进度增长）
        int arrowFill = (int) (ARROW_WIDTH * this.menu.getCookProgress());
        if (arrowFill > 0) {
            graphics.fill(this.leftPos + ARROW_X, this.topPos + ARROW_Y,
                    this.leftPos + ARROW_X + arrowFill, this.topPos + ARROW_Y + ARROW_HEIGHT, 0x50FFFFFF);
        }
        // 燃料燃烧火焰（自下而上随燃烧进度增长，熔炉风格）
        int flameFill = Math.round(FLAME_HEIGHT * this.menu.getLitProgress());
        if (flameFill > 0) {
            graphics.fill(this.leftPos + FLAME_X, this.topPos + FLAME_Y + FLAME_HEIGHT - flameFill,
                    this.leftPos + FLAME_X + FLAME_WIDTH, this.topPos + FLAME_Y + FLAME_HEIGHT, 0x50FFB600);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // 面板内无文字区域，不绘制标题
    }
}
