package me.shedaniel.cimtb;

import me.shedaniel.cimtb.mixin.MappedRegistryAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.AbstractMap;

public class Cimtb implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerTool(FabricMineableTags.SHEARS_MINEABLE, Items.SHEARS);
        registerTool(FabricMineableTags.SWORD_MINEABLE, new SwordItem(new MutableToolMaterial(0), 0, 0, new Item.Properties()) {});
        registerTool(BlockTags.MINEABLE_WITH_PICKAXE, new PickaxeItem(new MutableToolMaterial(0), 0, 0, new Item.Properties()) {});
        registerTool(BlockTags.MINEABLE_WITH_AXE, new AxeItem(new MutableToolMaterial(0), 0, 0, new Item.Properties()) {});
        registerTool(BlockTags.MINEABLE_WITH_SHOVEL, new ShovelItem(new MutableToolMaterial(0), 0, 0, new Item.Properties()) {});
        registerTool(BlockTags.MINEABLE_WITH_HOE, new HoeItem(new MutableToolMaterial(0), 0, 0, new Item.Properties()) {});
    }
    
    public static void registerTool(TagKey<Block> tag, Item item) {
        ToolHandler.TOOL_HANDLERS.add(new ToolHandler(new AbstractMap.SimpleImmutableEntry<>(tag, item)));
        if (BuiltInRegistries.ITEM.getResourceKey(item).isEmpty()) {
            ((MappedRegistryAccessor<Item>) BuiltInRegistries.ITEM).getUnregisteredIntrusiveHolders().remove(item);
        }
    }
    
    public static boolean isEffective(ItemStack stack, BlockState state) {
        return stack.isCorrectToolForDrops(state) || (!state.requiresCorrectToolForDrops() && stack.getDestroySpeed(state) > 1.0F);
    }
}
