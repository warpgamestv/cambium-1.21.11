package com.warpgames.cambium.item;

import com.warpgames.cambium.Cambium;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public class ModArmorMaterials {

        // Define the "Photovoltaic" material
        public static final ArmorMaterial PHOTOVOLTAIC = new ArmorMaterial(
                        15, // Durability multiplier
                        Map.of(
                                        ArmorType.BOOTS, 3,
                                        ArmorType.LEGGINGS, 5,
                                        ArmorType.CHESTPLATE, 7,
                                        ArmorType.HELMET, 3,
                                        ArmorType.BODY, 9 // Dog armor slot
                        ),
                        15, // Enchantability
                        net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_IRON, // Equip Sound
                        0.0F, // Toughness
                        0.0F, // Knockback Resistance
                        net.minecraft.tags.ItemTags.REPAIRS_COPPER_ARMOR, // Repair Ingredient Tag
                        ResourceKey.create(EquipmentAssets.ROOT_ID,
                                        Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "photovoltaic")));

        // public static final Holder<ArmorMaterial> PHOTOVOLTAIC_HOLDER =
        // Registry.registerForHolder(
        // BuiltInRegistries.ARMOR_MATERIAL,
        // Identifier.fromNamespaceAndPath(Cambium.MOD_ID, "photovoltaic"),
        // PHOTOVOLTAIC);
}