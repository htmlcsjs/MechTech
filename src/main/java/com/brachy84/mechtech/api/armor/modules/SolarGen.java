package com.brachy84.mechtech.api.armor.modules;

import com.brachy84.mechtech.api.armor.IArmorModule;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Supplier;

public class SolarGen implements IArmorModule {

    private final Supplier<ItemStack> item;
    private final long gen;
    private final int tier;

    public SolarGen(Supplier<ItemStack> item, long gen, int tier) {
        this.item = item;
        this.gen = gen;
        this.tier = tier;
    }

    @Override
    public void onTick(World world, EntityPlayer player, ItemStack modularArmorPiece, NBTTagCompound armorData) {
        if(world.isRemote) {
            double light = world.getLightFromNeighbors(new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ));
            if(light < 7) return;
            int generated = (int) (light / 15 * gen + 0.5);
            for(ItemStack stack : player.getArmorInventoryList()) {
                if(stack.isEmpty())
                    continue;
                IElectricItem item = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if(item == null)
                    continue;
                generated -= item.charge(generated, Integer.MAX_VALUE, false, false);
                if(generated <= 0)
                    break;
            }
        }
    }

    @Override
    public boolean canPlaceIn(EntityEquipmentSlot slot, ItemStack modularArmorPiece, IItemHandler modularSlots) {
        return slot == EntityEquipmentSlot.HEAD && IArmorModule.moduleCount(this, modularSlots) == 0;
    }

    @Override
    public void modifyArmorProperties(ISpecialArmor.ArmorProperties properties, EntityLivingBase entity, ItemStack modularArmorPiece, DamageSource source, double damage, EntityEquipmentSlot slot) {
    }

    @Override
    public ItemStack getAsItemStack() {
        return item.get();
    }

    @Override
    public String getLocalizedName() {
        return "mechtech.modules.solar_gen." + tier + ".name";
    }
}
