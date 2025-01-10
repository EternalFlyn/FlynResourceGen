package com.flyn.flyn_resource_gen

import com.google.common.collect.ImmutableMap
import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.registries.ForgeRegistries
import org.joml.Vector3f

fun CompoundTag.getBlock(tag: String): Block = getString(tag).let {
    val resource = ResourceLocation(it)
    if (ForgeRegistries.BLOCKS.containsKey(resource)) {
        ForgeRegistries.BLOCKS.getValue(resource)!!
    } else {
        Blocks.AIR
    }
}

fun CompoundTag.putBlock(tag: String, block: Block) {
    val str = ForgeRegistries.BLOCKS.getKey(block).toString()
    putString(tag, str)
}

fun ItemStack.setTag(nbt: CompoundTag.() -> Unit): ItemStack {
    if (tag == null)
        tag = CompoundTag()
    nbt(tag!!)
    return this
}

val DEFAULT_ITEM_TRANSFORMS = object : ItemTransforms(
    // ThirdPersonLeftHand
    ItemTransform(
        Vector3f(75f, 45f, 0f),
        Vector3f(0f, 2.5f / 16f, 0f),
        Vector3f(0.375f, 0.375f, 0.375f),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    // ThirdPersonRightHand
    ItemTransform(
        Vector3f(75f, 45f, 0f),
        Vector3f(0f, 2.5f / 16f, 0f),
        Vector3f(0.375f, 0.375f, 0.375f),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    // FirstPersonLeftHand
    ItemTransform(
        Vector3f(0f, 225f, 0f),
        Vector3f(0f, 0f, 0f),
        Vector3f(0.4f, 0.4f, 0.4f),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    // FirstPersonRightHand
    ItemTransform(
        Vector3f(0f, 45f, 0f),
        Vector3f(0f, 0f, 0f),
        Vector3f(0.4f, 0.4f, 0.4f),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    // Head
    ItemTransform(
        Vector3f(0f, 0f, 0f),
        Vector3f(0f, 14.25f / 16f, 0f),
        Vector3f(1f, 1f, 1f),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    // Gui
    ItemTransform(
        Vector3f(30f, 225f, 0f),
        Vector3f(0f, 0f, 0f),
        Vector3f(0.625f, 0.625f, 0.625f),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    // Ground
    ItemTransform(
        Vector3f(0f, 0f, 0f),
        Vector3f(0f, 3 / 16f, 0f),
        Vector3f(0.25f, 0.25f, 0.25f),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    // Fixed
    ItemTransform(
        Vector3f(0f, 0f, 0f),
        Vector3f(0f, 0f, 0f),
        Vector3f(0.5f, 0.5f, 0.5f),
        Vector3f(0.0f, 0.0f, 0.0f)
    ),
    ImmutableMap.builder<ItemDisplayContext, ItemTransform>().build()
) {}