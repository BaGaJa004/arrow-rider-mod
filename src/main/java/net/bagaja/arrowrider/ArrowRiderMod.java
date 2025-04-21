package net.bagaja.arrowrider;

import com.mojang.logging.LogUtils;
import net.bagaja.arrowrider.config.Config;
import net.bagaja.arrowrider.enchantment.ModEnchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ArrowRiderMod.MODID)
public class ArrowRiderMod {
    public static final String MODID = "arrowrider";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ArrowRiderMod() {
        // Suppress warning for now, standard practice in constructor
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register setup method
        modEventBus.addListener(this::commonSetup);

        // --- Register Enchantments ---
        ModEnchantments.register(modEventBus);

        // Register ourselves for server and other game events (if needed directly in this class)
        // ModEvents class uses static bus subscriber, so this might not be strictly necessary
        // unless you add non-static @SubscribeEvent methods here.
        MinecraftForge.EVENT_BUS.register(this);

        // --- Register Config ---
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        LOGGER.info("{} mod initialized.", MODID);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Arrow Rider common setup starting.");
        event.enqueueWork(() -> {
            // Log registration of our enchantment for confirmation
            if (ModEnchantments.ARROW_RIDER.isPresent())
                LOGGER.info("Registered Arrow Rider Enchantment: {}", ModEnchantments.ARROW_RIDER.getId());
        });
        LOGGER.info("Arrow Rider common setup complete.");
    }

    // No client setup needed for this specific mod's features
    // No server starting event needed directly here
}