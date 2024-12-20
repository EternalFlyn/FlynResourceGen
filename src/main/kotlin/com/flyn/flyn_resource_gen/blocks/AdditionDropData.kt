package com.flyn.flyn_resource_gen.blocks

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity

fun interface AdditionDropData<E : BlockEntity> {

    fun addDropData(stack: ItemStack, blockEntity: E): ItemStack

}