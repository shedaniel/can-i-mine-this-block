package me.shedaniel.cimtb;

import mcp.mobius.waila.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.Optional;

public class CimtbWailaPlugin implements IWailaPlugin, IBlockComponentProvider {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(this, TooltipPosition.BODY, Block.class);
    }
    
    @Override
    public void appendBody(ITooltip tooltipTexts, IBlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        Level world = accessor.getWorld();
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
            tooltipTexts.addLine(new TranslatableComponent("cimtb.harvestable.symbol." + harvestable).append(new TranslatableComponent("cimtb.harvestable").withStyle(ChatFormatting.GRAY)));
            tooltipTexts.addLine(new TranslatableComponent("cimtb.effective_tool").withStyle(ChatFormatting.GRAY).append(handler.getToolDisplay()));
            
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
                
                tooltipTexts.addLine(new TranslatableComponent("cimtb.harvest_level").withStyle(ChatFormatting.GRAY).append(new TextComponent(text).withStyle(
                        style -> style.withColor(TextColor.fromRgb(textColor[0]))
                )));
            }
        }
    }
}
