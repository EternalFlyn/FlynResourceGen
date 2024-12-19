package com.flyn.flyn_resource_gen

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
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

fun JsonObject.addArray(property: String, array: JsonArray.() -> Unit) {
    this.add(property, JsonArray().apply { array() })
}

fun JsonObject.addObject(property: String, obj: JsonObject.() -> Unit) {
    this.add(property, JsonObject().apply { obj() })
}

fun ItemStack.setTag(nbt: CompoundTag.() -> Unit): ItemStack {
    if (tag == null)
        tag = CompoundTag()
    nbt(tag!!)
    return this
}