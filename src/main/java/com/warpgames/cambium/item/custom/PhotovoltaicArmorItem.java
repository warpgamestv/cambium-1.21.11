package com.warpgames.cambium.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerLevel; // NEW
import net.minecraft.world.entity.EquipmentSlot; // NEW
import net.minecraft.world.item.equipment.ArmorMaterial; // NEW
import net.minecraft.world.item.equipment.ArmorType; // NEW
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import java.util.function.Consumer;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;

public class PhotovoltaicArmorItem extends Item {

    private final ArmorType armorType;

    public PhotovoltaicArmorItem(ArmorMaterial material, ArmorType type, Item.Properties properties) {
        super(properties.humanoidArmor(material, type));
        this.armorType = type;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slotId) {
        if (!level.isClientSide() && entity instanceof Player player) {

            // Check if the item is equipped in its designated slot
            if (player.getItemBySlot(slotId).equals(stack) && slotId == this.armorType.getSlot()) {

                if (isUnderSun(level, player)) {
                    // Self-Repair
                    if (stack.isDamaged() && level.getGameTime() % 80 == 0) {
                        stack.setDamageValue(stack.getDamageValue() - 1);
                    }

                    // Set Bonus
                    if (hasFullSet(player)) {
                        if (level.getGameTime() % 100 == 0) {
                            player.addEffect(
                                    new MobEffectInstance(MobEffects.REGENERATION, 110, 0, false, false, true));
                        }
                        if (!stack.isDamaged() && level.getGameTime() % 200 == 0 && player.getFoodData().needsFood()) {
                            player.getFoodData().eat(1, 0.5f);
                        }
                    }
                }
            }
        }
    }

    private boolean isUnderSun(ServerLevel level, Player player) {
        if (level.dimensionType().hasCeiling())
            return false;
        BlockPos pos = player.blockPosition().above();
        boolean isDay = level.getDayTime() % 24000L < 13000L;
        return level.getBrightness(LightLayer.SKY, pos) > 10 && isDay;
    }

    private boolean hasFullSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof PhotovoltaicArmorItem &&
                player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PhotovoltaicArmorItem &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof PhotovoltaicArmorItem &&
                player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof PhotovoltaicArmorItem;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
            net.minecraft.world.item.component.TooltipDisplay display, Consumer<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        tooltipComponents.accept(
                Component.literal("Solar Powered: Repairs in sunlight.").withStyle(net.minecraft.ChatFormatting.GOLD));
        tooltipComponents.accept(Component.literal("Full Set: Photosynthesis (Regen & Food)")
                .withStyle(net.minecraft.ChatFormatting.GREEN));
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}