package me.shedaniel.cimtb;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Optional;

public class CimtbJadePlugin implements IWailaPlugin, IBlockComponentProvider {
    @Override
    public void registerClient(IWailaClientRegistration registrar) {
        registrar.registerBlockComponent(this, Block.class);
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation("cimtb:cimtb_plugin");
    }

    @Override
    public void appendTooltip(ITooltip tooltipTexts, BlockAccessor accessor, IPluginConfig config) {
        try {
            Method method = config.getClass().getMethod("set", ResourceLocation.class, Object.class);
            method.invoke(config, new ResourceLocation("harvest_tool"), false);
            method.invoke(config, new ResourceLocation("harvest_tool.new_line"), false);
            method.invoke(config, new ResourceLocation("harvest_tool.effective_tool"), false);
            method.invoke(config, new ResourceLocation("harvest_tool.show_unbreakable"), false);
        } catch (Throwable ignored) {
        }
        BlockState state = accessor.getBlockState();
        Level world = accessor.getLevel();
        BlockPos position = accessor.getPosition();
        Player player = accessor.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (state.isAir() || state.getDestroySpeed(world, position) == -1)
            return;
        
        Optional<Pair<ToolHandler, Integer>> optionalEffectiveTool = ToolHandler.TOOL_HANDLERS.stream()
                .map(handler -> (Pair<ToolHandler, Integer>) new MutablePair<>(handler, handler.supportsBlock(state, player)))
                .min(Comparator.comparing(Pair::getRight, Comparator.nullsLast(Comparator.naturalOrder())));
        
        if (optionalEffectiveTool.isPresent()) {
            Pair<ToolHandler, Integer> entry = optionalEffectiveTool.get();
            if (entry.getRight() == null) return;
            
            ToolHandler handler = entry.getLeft();
            int level = entry.getRight();
            
            boolean harvestable = !state.requiresCorrectToolForDrops() || (!stack.isEmpty() && Cimtb.isEffective(stack, state));
            tooltipTexts.add(Component.translatable("cimtb.harvestable.symbol." + harvestable).append(Component.translatable("cimtb.harvestable").withStyle(ChatFormatting.GRAY)));
            tooltipTexts.add(Component.translatable("cimtb.effective_tool").withStyle(ChatFormatting.GRAY).append(handler.getToolDisplay()));
            
            if (level > 0) {
                int[] textColor = {11184810};
                String text = level + "";
                if (I18n.exists("cimtb.harvest_level.level." + text)) {
                    String translate = I18n.get("cimtb.harvest_level.level." + text);
                    if (translate.contains(":")) {
                        textColor[0] = Integer.parseInt(translate.substring(0, translate.indexOf(':')));
                        translate = translate.substring(translate.indexOf(':') + 1);
                    }
                    
                    text = I18n.get("cimtb.harvest_level.level.format", translate, level);
                }
                
                tooltipTexts.add(Component.translatable("cimtb.harvest_level").withStyle(ChatFormatting.GRAY).append(Component.literal(text).withStyle(
                        style -> style.withColor(TextColor.fromRgb(textColor[0]))
                )));
            }
        }
    }
}
