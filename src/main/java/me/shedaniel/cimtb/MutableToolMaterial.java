package me.shedaniel.cimtb;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class MutableToolMaterial implements Tier {
    public int miningLevel;
    
    public MutableToolMaterial(int miningLevel) {
        this.miningLevel = miningLevel;
    }
    
    @Override
    public int getUses() {
        return 1;
    }
    
    @Override
    public float getSpeed() {
        return 1.01f;
    }
    
    @Override
    public float getAttackDamageBonus() {
        return 0;
    }
    
    @Override
    public int getLevel() {
        return miningLevel;
    }
    
    @Override
    public int getEnchantmentValue() {
        return 1;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }
}