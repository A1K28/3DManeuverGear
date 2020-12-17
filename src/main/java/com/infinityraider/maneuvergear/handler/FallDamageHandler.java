package com.infinityraider.maneuvergear.handler;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.capability.CapabilityFallBoots;
import com.infinityraider.maneuvergear.registry.ItemRegistry;
import com.infinityraider.maneuvergear.utility.ManeuverGearHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FallDamageHandler {
    private static final FallDamageHandler INSTANCE = new FallDamageHandler();

    private FallDamageHandler() {}

    public static FallDamageHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerFall(LivingHurtEvent event) {
        if(!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }
        if(!event.getSource().damageType.equals(DamageSource.FALL.getDamageType())) {
            return;
        }
        PlayerEntity player = (PlayerEntity) event.getEntity();
        // check if player is wearing maneuver gear compatible boots
        if(CapabilityFallBoots.areFallBoots(player.getItemStackFromSlot(EquipmentSlotType.FEET))) {
            // check if player is wearing maneuver gear
            ItemStack gear = ManeuverGearHelper.findManeuverGear(player);
            if(gear != null && gear.getItem() == ItemRegistry.getInstance().itemManeuverGear) {
                event.setAmount((1.0F - ManeuverGear.instance.getConfig().getBootFallDmgReduction()) * event.getAmount());
            }
        }

    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onWitherDeath(LivingDropsEvent event) {
        if(ItemRegistry.getInstance().itemRecord == null) {
            return;
        }
        if(!(event.getEntity() instanceof WitherEntity)) {
            return;
        }
        Entity killer = event.getSource().getTrueSource();
        if(event.isRecentlyHit() && killer != null && killer instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) killer;
            ItemStack left = player.getHeldItem(Hand.MAIN_HAND);
            ItemStack right = player.getHeldItem(Hand.OFF_HAND);
            if(isValidStack(left) && isValidStack(right)) {
                ItemEntity drop = new ItemEntity(
                        event.getEntity().getEntityWorld(), event.getEntity().getPosX(), event.getEntity().getPosY()+0.5D, event.getEntity().getPosZ(),
                        new ItemStack(ItemRegistry.getInstance().itemRecord));
                event.getDrops().add(drop);
            }
        }
    }

    private boolean isValidStack(ItemStack stack) {
        return stack != null && stack.getItem() == ItemRegistry.getInstance().itemManeuverGearHandle;
    }
}
