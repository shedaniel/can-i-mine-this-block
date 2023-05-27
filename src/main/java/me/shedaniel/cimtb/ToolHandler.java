package me.shedaniel.cimtb;

import com.google.common.base.Suppliers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ToolHandler {
    public static final List<ToolHandler> TOOL_HANDLERS = new ArrayList<>();
    
    public final TagKey<Block> tag;
    public final Item defaultTool;
    private final Supplier<Integer> maximumLevel = Suppliers.memoize(() -> {
        int highest = 3;
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof TieredItem tieredItem) {
                int miningLevel = tieredItem.getTier().getLevel();
                if (miningLevel > highest) {
                    highest = miningLevel;
                }
            }
        }
        return highest;
    });
    
    public ToolHandler(Map.Entry<TagKey<Block>, Item> entry) {
        this.tag = entry.getKey();
        this.defaultTool = entry.getValue();
    }
    
    public Integer supportsBlock(BlockState state, Player user) {
        ItemStack itemStack = new ItemStack(defaultTool);
        if (defaultToolSupportsMutableLevel()) {
            for (int level = 0; level <= maximumLevel.get(); level++) {
                setDefaultToolSupportsMutableLevel(level);
                boolean effective = Cimtb.isEffective(itemStack, state);
                if (effective) {
                    return level;
                }
            }
        }
        boolean effective = Cimtb.isEffective(itemStack, state);
        if (effective) {
            return getToolMiningLevel(itemStack, state, user);
        }
        return null;
    }
    
    private boolean defaultToolSupportsMutableLevel() {
        return defaultTool instanceof TieredItem tieredItem && tieredItem.getTier() instanceof MutableToolMaterial;
    }
    
    private void setDefaultToolSupportsMutableLevel(int level) {
        ((MutableToolMaterial) ((TieredItem) defaultTool).getTier()).miningLevel = level;
    }
    
    private int getToolMiningLevel(ItemStack stack, BlockState state, LivingEntity user) {
        Item item = stack.getItem();
        if (item instanceof TieredItem tieredItem)
            return tieredItem.getTier().getLevel();
        return 0;
    }
    
    public Component getToolDisplay() {
        return Component.translatable("cimtb.effective_tool." + tag.location().getNamespace() + "." + tag.location().getPath().replace('/', '.')).withStyle(ChatFormatting.DARK_GREEN);
    }
}