package net.bagaja.arrowrider.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class ArrowRiderEnchantment extends Enchantment {

    protected ArrowRiderEnchantment() {
        // Rarity: RARE seems reasonable
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinCost(int pEnchantmentLevel) {
        return 20; // Lowered cost slightly
    }

    @Override
    public int getMaxCost(int pEnchantmentLevel) {
        return 45;
    }

    @Override
    public int getMaxLevel() {
        return 1; // Only one level needed
    }

    @Override
    public boolean canEnchant(ItemStack pStack) {
        // Redundant check with category, but safe
        return pStack.getItem() instanceof BowItem || pStack.getItem() instanceof CrossbowItem || super.canEnchant(pStack);
    }

    /**
     * Make incompatible with Multishot (Infinity check REMOVED as requested).
     */
    @Override
    public boolean checkCompatibility(Enchantment pOther) {
        // REMOVED: if (pOther == Enchantments.INFINITY_ARROWS) return false;
        if (pOther == Enchantments.MULTISHOT) {
            return false; // Still incompatible with Multishot to avoid potential weirdness
        }
        return super.checkCompatibility(pOther);
    }
}