package me.shedaniel.cimtb.mixin;

import me.shedaniel.cimtb.ToolHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow
    public abstract Item getItem();
    
    @Inject(method = "isIn", at = @At("HEAD"), cancellable = true)
    private void isIn(Tag<Item> tag, CallbackInfoReturnable<Boolean> cir) {
        if (!(tag instanceof Tag.Identified)) return;
        for (ToolHandler toolHandler : ToolHandler.TOOL_HANDLERS) {
            if (toolHandler.defaultTool == getItem() && toolHandler.tag.getId() == ((Tag.Identified<Item>) tag).getId()) {
                cir.setReturnValue(true);
            }
        }
    }
}
