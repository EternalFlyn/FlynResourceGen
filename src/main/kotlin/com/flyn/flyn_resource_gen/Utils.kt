package com.flyn.flyn_resource_gen

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraftforge.registries.ForgeRegistries

fun CompoundTag.getItemId(tag: String): Item = getString(tag).let {
    val resource = ResourceLocation(it)
    if (ForgeRegistries.ITEMS.containsKey(resource)) {
        ForgeRegistries.ITEMS.getValue(resource)!!
    } else {
        Items.AIR
    }
}

fun CompoundTag.putItemId(tag: String, item: Item) {
    val str = ForgeRegistries.ITEMS.getKey(item).toString()
    putString(tag, str)
}