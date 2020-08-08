package me.shedaniel.cimtb;

import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CimtbWailaPlugin implements IWailaPlugin, IComponentProvider {
    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(this, TooltipPosition.BODY, Block.class);
    }
    
    @Override
    public void appendBody(List<Text> tooltipTexts, IDataAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        World world = accessor.getWorld();
        BlockPos position = accessor.getPosition();
        PlayerEntity player = accessor.getPlayer();
        ItemStack stack = player.getMainHandStack();
        if (state.isAir() || state.getHardness(world, position) == -1)
            return;
        
        Optional<Pair<ToolHandler, Integer>> optionalEffectiveTool = ToolHandler.TOOL_HANDLERS.stream()
                .map(handler -> new Pair<>(handler, handler.supportsBlock(state, player)))
                .min(Comparator.comparing(Pair::getRight, Comparator.nullsLast(Comparator.naturalOrder())));
        
        if (optionalEffectiveTool.isPresent()) {
            Pair<ToolHandler, Integer> entry = optionalEffectiveTool.get();
            if (entry.getRight() == null) return;
            
            ToolHandler handler = entry.getLeft();
            int level = entry.getRight();
            
            boolean harvestable = !state.isToolRequired() || (!stack.isEmpty() && Cimtb.isEffective(stack, state));
            tooltipTexts.add(new TranslatableText("cimtb.harvestable.symbol." + harvestable).append(new TranslatableText("cimtb.harvestable").formatted(Formatting.GRAY)));
            tooltipTexts.add(new TranslatableText("cimtb.effective_tool").formatted(Formatting.GRAY).append(handler.getToolDisplay()));
            
            if (level > 0) {
                int[] textColor = {11184810};
                String text = level + "";
                if (I18n.hasTranslation("cimtb.harvest_level.level." + text)) {
                    String translate = I18n.translate("cimtb.harvest_level.level." + text);
                    if (translate.contains(":")) {
                        textColor[0] = Integer.parseInt(translate.substring(0, translate.indexOf(':')));
                        translate = translate.substring(translate.indexOf(':') + 1);
                    }
                    
                    text = I18n.translate("cimtb.harvest_level.level.format", translate, level);
                }
                
                tooltipTexts.add(new TranslatableText("cimtb.harvest_level").formatted(Formatting.GRAY).append(new LiteralText(text).styled(
                        style -> style.withColor(TextColor.fromRgb(textColor[0]))
                )));
            }
        }
    }
}
