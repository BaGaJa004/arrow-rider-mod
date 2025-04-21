package net.bagaja.arrowrider.config;

import com.mojang.logging.LogUtils;
import net.bagaja.arrowrider.ArrowRiderMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = ArrowRiderMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // --- General Settings ---
    private static final ForgeConfigSpec.BooleanValue ENABLE_ARROW_RIDING = BUILDER
            .comment("Enable the Arrow Rider enchantment functionality. If false, the enchantment exists but does nothing.")
            .define("enableArrowRiding", true);

    // You could add more options here later, e.g., enableDamage, damageAmount etc.

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // --- Loaded Values ---
    public static boolean enableArrowRiding;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            enableArrowRiding = ENABLE_ARROW_RIDING.get();
            // --- Use the Config class's own logger ---
            LOGGER.info("Arrow Rider config loaded: enableArrowRiding={}", enableArrowRiding);
        }
    }
}