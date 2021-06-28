package me.shedaniel.cimtb;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ToolHandler {
    public static final List<ToolHandler> TOOL_HANDLERS = Lists.newArrayList();
    
    public final Tag.Identified<Item> tag;
    public final Item defaultTool;
    private final Supplier<Integer> maximumLevel = Suppliers.memoize(() -> {
        int highest = 3;
        for (Item item : Registry.ITEM) {
            if (item instanceof ToolItem toolItem) {
                int miningLevel = toolItem.getMaterial().getMiningLevel();
                if (miningLevel > highest) {
                    highest = miningLevel;
                }
            }
        }
        return highest;
    });
    
    public ToolHandler(Map.Entry<Tag<Item>, Item> entry) {
        this.tag = (Tag.Identified<Item>) entry.getKey();
        this.defaultTool = entry.getValue();
    }
    
    private boolean supportsTool(ItemStack stack) {
        return stack.isIn(tag);
    }
    
    public Integer supportsBlock(BlockState state, PlayerEntity user) {
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
        return defaultTool instanceof ToolItem && ((ToolItem) defaultTool).getMaterial() instanceof MutableToolMaterial;
    }
    
    private void setDefaultToolSupportsMutableLevel(int level) {
        ((MutableToolMaterial) ((ToolItem) defaultTool).getMaterial()).miningLevel = level;
    }
    
    private int getToolMiningLevel(ItemStack stack, BlockState state, LivingEntity user) {
        Item item = stack.getItem();
        if (item instanceof DynamicAttributeTool)
            return ((DynamicAttributeTool) item).getMiningLevel(tag, state, stack, user);
        if (item instanceof ToolItem)
            return ((ToolItem) item).getMaterial().getMiningLevel();
        return 0;
    }
    
    public Text getToolDisplay() {
        return new TranslatableText("cimtb.effective_tool." + tag.getId().getNamespace() + "." + tag.getId().getPath()).formatted(Formatting.DARK_GREEN);
    }
}