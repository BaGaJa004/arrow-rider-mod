package net.bagaja.arrowrider.event; // Use a sub-package for events

import net.bagaja.arrowrider.ArrowRiderMod;
import net.bagaja.arrowrider.config.Config; // Import Config
import net.bagaja.arrowrider.enchantment.ModEnchantments; // Import ModEnchantments
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArrowRiderMod.MODID)
public class ModEvents {

    // NBT Key specific to this mod
    private static final String NBT_KEY_RIDER_UUID = ArrowRiderMod.MODID + ":rider_uuid";

    // Check if player needs dismounting if arrow dies mid-flight
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Run on server side, at the end of the tick
        if (event.phase == TickEvent.Phase.END && event.player != null && !event.player.level().isClientSide) {
            Player player = event.player;

            // Check if player is riding something
            if (player.isPassenger()) {
                Entity vehicle = player.getVehicle();
                // Check if riding an arrow that is no longer valid
                if (vehicle instanceof AbstractArrow riddenArrow && (!riddenArrow.isAlive() || riddenArrow.isRemoved())) {
                    player.stopRiding();
                    // Optional: Log dismount due to dead arrow
                }
            }
        }
    }

    // Start riding arrow on firing
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        // Check config first
        if (!Config.enableArrowRiding) {
            return;
        }

        if (event.getLevel().isClientSide) {
            return;
        }

        if (event.getEntity() instanceof AbstractArrow arrow) {
            Entity shooter = arrow.getOwner();
            if (shooter instanceof Player player) {
                // Check item used (same logic as before)
                ItemStack shootingStack = player.getUseItem();
                if (shootingStack.isEmpty() || !(shootingStack.getItem() instanceof net.minecraft.world.item.BowItem || shootingStack.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
                    shootingStack = player.getMainHandItem();
                    if(!(shootingStack.getItem() instanceof net.minecraft.world.item.BowItem || shootingStack.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
                        shootingStack = player.getOffhandItem();
                        if(!(shootingStack.getItem() instanceof net.minecraft.world.item.BowItem || shootingStack.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
                            shootingStack = ItemStack.EMPTY;
                        }
                    }
                }

                if (!shootingStack.isEmpty()) {
                    int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ARROW_RIDER.get(), shootingStack);
                    if (level > 0) {
                        // Start riding
                        player.startRiding(arrow, true);

                        // Add NBT marker
                        CompoundTag nbt = arrow.getPersistentData();
                        nbt.putString(NBT_KEY_RIDER_UUID, player.getUUID().toString());
                        // Optional: Log ride start
                    }
                }
            }
        }
    }

    // Stop riding on arrow impact
    @SubscribeEvent
    public static void onArrowImpact(ProjectileImpactEvent event) {
        // No need to check config here, we only care if the arrow *was* marked

        if (event.getProjectile().level().isClientSide) {
            return;
        }

        if (event.getProjectile() instanceof AbstractArrow arrow) {
            CompoundTag nbt = arrow.getPersistentData();

            // Check if it was a rider arrow
            if (nbt.contains(NBT_KEY_RIDER_UUID, Tag.TAG_STRING)) {
                UUID shooterUUID;
                try {
                    shooterUUID = UUID.fromString(nbt.getString(NBT_KEY_RIDER_UUID));
                } catch (IllegalArgumentException e) {
                    arrow.discard();
                    return;
                }

                if (arrow.level() instanceof ServerLevel serverLevel) {
                    Player shooter = serverLevel.getPlayerByUUID(shooterUUID);

                    // If player exists and is currently riding THIS arrow
                    if (shooter != null && shooter.isAlive() && shooter.getVehicle() == arrow) {
                        // Stop riding
                        shooter.stopRiding();

                        // Play sound
                        Vec3 impactPos = event.getRayTraceResult().getLocation();
                        serverLevel.playSound(null, impactPos.x(), impactPos.y(), impactPos.z(),
                                SoundEvents.PLAYER_ATTACK_KNOCKBACK, // Or another sound
                                SoundSource.PLAYERS, 0.8F, 1.0F);
                        // Optional: Log dismount on impact
                    }
                }

                // Remove the arrow regardless of whether the player was still riding
                arrow.discard();
            }
        }
    }
}