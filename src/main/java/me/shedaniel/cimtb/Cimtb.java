package me.shedaniel.cimtb;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tag.Tag;

import java.util.AbstractMap;

public class Cimtb implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerTool(FabricToolTags.SHEARS, Items.SHEARS);
        registerTool(FabricToolTags.SWORDS, new SwordItem(new MutableToolMaterial(0), 0, 0, new Item.Settings()) {});
        registerTool(FabricToolTags.PICKAXES, new PickaxeItem(new MutableToolMaterial(0), 0, 0, new Item.Settings()) {});
        registerTool(FabricToolTags.AXES, new AxeItem(new MutableToolMaterial(0), 0, 0, new Item.Settings()) {});
        registerTool(FabricToolTags.SHOVELS, new ShovelItem(new MutableToolMaterial(0), 0, 0, new Item.Settings()) {});
        registerTool(FabricToolTags.HOES, new HoeItem(new MutableToolMaterial(0), 0, 0, new Item.Settings()) {});
    }
    
    public static void registerTool(Tag<Item> tag, Item item) {
        ToolHandler.TOOL_HANDLERS.add(new ToolHandler(new AbstractMap.SimpleImmutableEntry<>(tag, item)));
    }
    
    public static boolean isEffective(ItemStack stack, BlockState state) {
        return stack.isEffectiveOn(state) || (!state.isToolRequired() && stack.getMiningSpeed(state) > 1.0F);
    }
}
