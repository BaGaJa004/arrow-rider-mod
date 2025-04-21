package net.bagaja.arrowrider.enchantment; // Use a sub-package for enchantments

import net.bagaja.arrowrider.ArrowRiderMod;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ArrowRiderMod.MODID);

    // --- Register Arrow Rider Enchantment ---
    public static final RegistryObject<Enchantment> ARROW_RIDER =
            ENCHANTMENTS.register("arrow_rider", ArrowRiderEnchantment::new);

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}