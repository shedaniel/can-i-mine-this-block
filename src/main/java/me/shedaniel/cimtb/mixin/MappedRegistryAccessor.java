package me.shedaniel.cimtb.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {
    @Nullable
    @Accessor
    Map<T, Holder.Reference<T>> getUnregisteredIntrusiveHolders();
}
